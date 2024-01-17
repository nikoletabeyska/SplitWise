package server;

import java.util.HashMap;
import java.util.Map;

public class RepositoryImplementationMapping {

    private static Map<Class<?>, Object> mapOfClasses =new HashMap<>();

    static void  addOrReplace(Object o)
    {
        if(mapOfClasses.containsKey(o.getClass())) {
            mapOfClasses.replace(o.getClass(),0);
        }
        else {
            mapOfClasses.put(o.getClass(),o);
        }

    }
    public static Object get(Class<?> c)
    {
        if(mapOfClasses.containsKey(c))
            return mapOfClasses.get(c);
        else
            return null;
    }


}

