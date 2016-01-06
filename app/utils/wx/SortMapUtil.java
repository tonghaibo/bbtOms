package utils.wx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;


/**
 * 
 * <p>Title: SortMapUtil.java</p> 
 * <p>Description: map排序</p> 
 * <p>Company: higegou</p> 
 * @author  ctt
 * date  2015年10月23日  下午5:49:49
 * @version
 */
public class SortMapUtil {
	
	public static SortedMap<String,String> sort(HashMap<String, String> parames){
		SortedMap<String, String> signParams = new TreeMap<String, String>();
		Set<Entry<String,String>> entry1=parames.entrySet();  
		Iterator<Entry<String,String>> it=entry1.iterator();  
		while(it.hasNext()){  
			Entry<String,String> entry=it.next();  
			System.out.println("排序之后:"+entry.getKey()+" 值"+entry.getValue());
			signParams.put(entry.getKey(), entry.getValue());
		}
		return signParams;
	}
	
}
