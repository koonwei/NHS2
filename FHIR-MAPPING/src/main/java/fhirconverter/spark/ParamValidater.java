package fhirconverter.spark;

import java.util.HashMap;

public class ParamValidater<V> {

    private Class<V> valueClass;

    public ParamValidater(Class<V> valueClass){
        this.valueClass = valueClass;
    }

    public boolean isValid(HashMap data)
    {
        //mapper(data, class)
        return true;
    }
}
