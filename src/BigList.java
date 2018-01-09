import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BigList<T> {
	int size;
	List<List<T>> list;
	
	public BigList(BigInteger n) {
		size = n.divide(BigInteger.valueOf(Integer.MAX_VALUE)).intValue() + 1;
		list = new ArrayList<List<T>>();
		for (int i = 0; i < size; i++) {
			list.add(new ArrayList<T>());
		}
	}
	
	public int size() {
		return size;
	}
}
