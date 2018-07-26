package mapreduce;

import core.Complex;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ComplexWriteable implements Writable {

    private FloatWritable real;
    private FloatWritable im;

    public Complex get() {
        return new Complex(real.get(), im.get());
    }

    public void set(Complex c) {
        real.set(c.getReal());
        im.set(c.getIm());
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        real.write(dataOutput);
        im.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        real.readFields(dataInput);
        im.readFields(dataInput);
    }
}
