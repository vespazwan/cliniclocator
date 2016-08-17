package ihs.com.cliniclocator;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by EMA on 9/3/2016.
 */
public class MapComparator implements Comparator<Map<String, String>> {

    private final String jarak;

    public MapComparator(String jarak) {
        this.jarak = jarak;
    }

    @Override
    public int compare(Map<String, String> lhs, Map<String, String> rhs) {
        Double firstValue = Double.parseDouble(lhs.get(jarak));
        Double secondValue = Double.parseDouble(rhs.get(jarak));
        return firstValue.compareTo(secondValue);
    }
}
