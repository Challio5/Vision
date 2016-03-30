package nl.vision.control.converter;

import javafx.util.StringConverter;
import nl.vision.model.Filter;

import java.util.HashMap;

/**
 * Created by rob on 19-10-15.
 */
public class FilterConverter extends StringConverter<Filter> {
    private HashMap<String, Filter> map = new HashMap<>();

    @Override
    public String toString(Filter filter) {
        map.put(filter.getClass().getSimpleName(), filter);
        return filter.getClass().getSimpleName();
    }

    @Override
    public Filter fromString(String string) {
        return map.get(string);
    }
}
