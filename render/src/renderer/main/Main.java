package renderer.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;

import raytracing.load.BasicLoader;
import raytracing.load.CameraLoader;
import raytracing.load.ConfLoader;
import raytracing.load.ModelLoader;
import raytracing.log.ILog;
import raytracing.log.LogFactory;
import raytracing.mapreduce.RayTracerDriver;
import raytracing.mapreduce.RayTracerDriver.PARAMS;
import raytracing.photon.PhotonLoader;
import raytracing.trace.CameraTrace;
import renderer.utils.DirectoryChecker;

public class Main {

	private static RayTracing rayTracing = new RayTracing();
	private static int superSamplingTimes = 3;

	/** Time Log :
	 * Map Reduce run a image with softshadow consumes 1769.226s.
	 * Rendering a local image consumes 467.977 s
	 */

	public static void main(String[] args) throws IOException {
		long tStart = System.currentTimeMillis();

		LogFactory.setParams("log", BasicLoader.ENV.NATIVE);

		ModelLoader ml = new ModelLoader("tmp.mods", null, BasicLoader.ENV.NATIVE);
		ml.parse(rayTracing.getScene());
		ml.close();

		CameraLoader cl = new CameraLoader("tmp.camera", null, BasicLoader.ENV.NATIVE);
		Camera origCamera = new Camera(new Vec3d(), new Vec3d(), new Vec3d(), 60.0, 480, 640);
		ArrayList<CameraTrace> cats = new ArrayList<CameraTrace>();
		cl.parse(origCamera, cats);
		cl.close();

		HashMap<String, String> opts = new HashMap<String, String>();
		ConfLoader confLoader = new ConfLoader("tmp.conf", null, BasicLoader.ENV.NATIVE);
		confLoader.parse(opts);
		confLoader.close();

		rayTracing.setMaxRayDepth(opts.getOrDefault("MAX_RAY_DEPTH", "5"));
		if (opts.getOrDefault(RayTracerDriver.PARAMS.IS_ON_SOFT_SHADOW.name(), "off").toLowerCase().equals("on")) {
			rayTracing.setSoftShadow(opts.get(PARAMS.SOFT_SHADOW_NUMBER.name()));
		} else {
			rayTracing.setSoftShadow(false);
		}
		superSamplingTimes = Integer.parseInt(opts.getOrDefault("SUPER_SAMPLING_TIMES", "3"));

		render(origCamera, cats);

		ILog log = LogFactory.getInstance(RayTracerDriver.PARAMS.ILOG.name());
		log.close();

		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000.0;
		System.out.println("Time Consume : " + elapsedSeconds + " s");
	}

	public static void render(Camera origCamera, ArrayList<CameraTrace> cats) {
		Camera camera = origCamera;
		DirectoryChecker.dirCheck("image", true);

		int i = 0;
		render(camera, i);
		for (CameraTrace cat : cats) {
			cat.setInitCameraLocation(origCamera);
			while ((camera = cat.getNextCameraFrame()) != null) {
				i ++;
				origCamera = camera;
				render(camera, i);
			}
		}

	}

	public static int x = 0, y = 0;
	public static void render(Camera camera, int id) {
		camera.viewFrame(id);

		int width = camera.getCols(), height = camera.getRows();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

//	    rayTracing.setPhotonLoader(new PhotonLoader("tmp.renderer.photon"), 1.0);

		int times = superSamplingTimes;
		for (y = 0; y < height; ++y) {
			if (y % 100 == 0) System.out.println("Rows : " + y);
			for (x = 0; x < width; ++x) {
				Vec3d rgb = new Vec3d();
				ArrayList<Ray> rays = new ArrayList<Ray>();
				camera.getSuperSamplingRays(x, y, times, rays);
				for (Ray ssray : rays) {
					Vec3d p = new Vec3d();
					rayTracing.trace(ssray, p, 0);
					rgb.addToThis(p);
				}
				double invTimes = 1.0 / (times * times);
				rgb.mulToThis(new Vec3d(invTimes));

				image.setRGB(x, y, rgb.getRGB());
			}
		}

		JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
		jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		jpegParams.setCompressionQuality(1f);

		try {
			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
			// specifies where the jpg image has to be written
			FileImageOutputStream fios = new FileImageOutputStream(new File("image/image" + id + ".jpg"));
			writer.setOutput(fios);

			// writes the file with given compression level
			// from your JPEGImageWriteParam instance
			writer.write(null, new IIOImage(image, null, null), jpegParams);
			writer.reset();
			fios.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
