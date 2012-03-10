package patch;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

public class GeneratedFile {
	OutputStream out = null;
	public GeneratedFile(String fileName) {
		try {
			out = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(int data) {
		try {
			out.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(Collection<Byte> data) {
		try {
			for (Iterator<Byte> i = data.iterator(); i.hasNext();) {
				out.write(i.next().byteValue());				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int writeULeb128(int value) {
        long remaining = (value & 0xFFFFFFFFL) >> 7;
        long lValue = value;
        int count = 0;

        while (remaining != 0) {
            try {
				out.write((int)(lValue & 0x7f) | 0x80);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            lValue = remaining;
            remaining >>= 7;
            count++;
        }

        try {
			out.write((int)(lValue & 0x7f));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return count + 1;
    }
	
	
	public void write16bit(long data) {
		byte[] output = new byte[2];
		
		for(int i = 0; i < 2; ++i) {
			output[i] = (byte)((data >> (i*8)) & 0xFF);
		}
		try {
			out.write(output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(String data) {
		try {
			byte[] b = new byte[data.length()];
			data.getBytes(0, data.length(), b, 0);
			out.write(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(long data) {
		byte[] output = new byte[4];
		
		for(int i = 0; i < 4; ++i) {
			output[i] = (byte)((data >> (i*8)) & 0xFF);
		}
		try {
			out.write(output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
