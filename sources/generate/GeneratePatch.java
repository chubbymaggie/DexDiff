package generate;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import patch.DexOriginalFile;
import patch.GeneratedFile;

public class GeneratePatch {

	public static void main(String[] args) {
		DexOriginalFile original = new DexOriginalFile();
		DexOriginalFile update = new DexOriginalFile();
		original.setDumpOff();
		update.setDumpOff();
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile("testfiles/connect2.dex", "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		original.setRandomAccessFile(raf);
		original.parse();
		try {
			raf = new RandomAccessFile("testfiles/connect3.dex", "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		update.setRandomAccessFile(raf);
		update.parse();
		
		GeneratedFile patchFile = new GeneratedFile("out/connect.patch");
		GeneratedFile dataFile = new GeneratedFile("out/data.patch");
		patchFile.write(((Long)update.getStringDataItemOffset()).toString() +"\n");
		patchFile.write(((Long)update.getTypeListOffset()).toString() +"\n");
		
		String[] data1 = { "a", "b", "c", "d", "e", "f", "g" };
		String[] data2 = { "a", "f", "b", "c", "e", "f", "g", "s" };
		List<PCommand> l = lcs2(original.stringData, update.stringData);
		//List<PCommand> l = lcs2(data1, data2);
		
		
		ListIterator<PCommand> it = l.listIterator(l.size());
		PCommand c = it.previous();
		while (true) {
			int val = c.type;
			int nx = c.type;
			int count = 0;
			while (nx == val) {
				if(nx == 1) {
					dataFile.write(c.data);
					dataFile.write("\n");
				}
				++count;
				
				if (!it.hasPrevious())
					break;
				
				c = it.previous();
				nx = c.type;
			}
			
			patchFile.write(((Integer)val).toString() + " " + ((Integer)count).toString() + "\n");
			
			if (!it.hasPrevious()) {
				if(nx != val) {
					if(nx == 1) {
						dataFile.write(c.data);
						dataFile.write("\n");
					}
					patchFile.write(((Integer)nx).toString() + " " + "1\n");
				}
				break;
			}
		}
		
		patchFile.write("4\n");
		
		l = lcs2(original.typeIds, update.typeIds, original.stringData, update.stringData);
		it = l.listIterator(l.size());
		c = it.previous();
		while (true) {
			int val = c.type;
			int nx = c.type;
			int count = 0;
			while (nx == val) {
				if(nx == 1) {
					dataFile.write(((Integer)c.index).toString());
					dataFile.write("\n");
				}
				
				++count;
				
				if (!it.hasPrevious())
					break;
				
				c = it.previous();
				nx = c.type;
			}
			
			patchFile.write(((Integer)val).toString() + " " + ((Integer)count).toString() + "\n");
			
			if (!it.hasPrevious()) {
				if(nx != val) {
					if(nx == 1) {
						dataFile.write(((Integer)c.index).toString());
						dataFile.write("\n");
					}
					patchFile.write(((Integer)nx).toString() + " " + "1\n");
				}
				break;
			}
		}
		
		
		System.out.print("done");
	}
	
	
	public static List<PCommand> lcs2(String[] a, String[] b) {
	    int[][] lengths = new int[a.length+1][b.length+1];
	 
	    // row 0 and column 0 are initialized to 0 already
	 
	    for (int i = 0; i < a.length; i++)
	        for (int j = 0; j < b.length; j++)
	            if (a[i].equals(b[j]))
	                lengths[i+1][j+1] = lengths[i][j] + 1;
	            else
	                lengths[i+1][j+1] =
	                    Math.max(lengths[i+1][j], lengths[i][j+1]);
	 
	    // read the substring out from the matrix
	    List<PCommand> l = new LinkedList<PCommand>();
	    for (int x = a.length, y = b.length;
	         x != 0 && y != 0; ) {
	        if (lengths[x][y] == lengths[x-1][y]) {
	        	l.add(new PCommand(2, null));
	        	x--;
	        } else if (lengths[x][y] == lengths[x][y-1]) {
	        	byte[] by = new byte[b[y-1].length() - 1];
	        	b[y-1].getBytes(1, b[y-1].length(), by, 0);
	        	l.add(new PCommand(1, by));
	        	y--;
	            
	        } else {
	        	l.add(new PCommand(0, null));
	        	
	        	x--;
	            y--;
	        }
	    }
	 
	    return l;
	}
	
	public static List<PCommand> lcs2(int[] a, int[] b, String[] aData, String[] bData) {
	    int[][] lengths = new int[a.length+1][b.length+1];
	 
	    // row 0 and column 0 are initialized to 0 already
	 
	    for (int i = 0; i < a.length; i++)
	        for (int j = 0; j < b.length; j++)
	            if (aData[a[i]].equals(bData[b[j]]))
	                lengths[i+1][j+1] = lengths[i][j] + 1;
	            else
	                lengths[i+1][j+1] =
	                    Math.max(lengths[i+1][j], lengths[i][j+1]);
	 
	    // read the substring out from the matrix
	    List<PCommand> l = new LinkedList<PCommand>();
	    for (int x = a.length, y = b.length;
	         x != 0 && y != 0; ) {
	        if (lengths[x][y] == lengths[x-1][y]) {
	        	l.add(new PCommand(2, null));
	        	x--;
	        } else if (lengths[x][y] == lengths[x][y-1]) {
	        	l.add(new PCommand(1, b[y-1]));
	        	y--;
	            
	        } else {
	        	l.add(new PCommand(0, null));
	        	
	        	x--;
	            y--;
	        }
	    }
	 
	    return l;
	}
	
	private static class PCommand {
		public int type;
		byte[] data;
		int index;
		
		public PCommand(int type, byte[] data) {
			this.type = type;
			this.data = data;
		}
		
		public PCommand(int type, int index) {
			this.type = type;
			this.index = index;
		}
	}
}
