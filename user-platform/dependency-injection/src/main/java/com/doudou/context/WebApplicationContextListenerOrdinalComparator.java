package com.doudou.context;

import javax.annotation.Priority;
import java.util.Comparator;

/**
 * {@link WebApplicationContextListener} 优先级比较器
 */
public class WebApplicationContextListenerOrdinalComparator implements Comparator<WebApplicationContextListener> {

    public static final Comparator<WebApplicationContextListener> INSTANCE = new WebApplicationContextListenerOrdinalComparator();

    private WebApplicationContextListenerOrdinalComparator() {
    }

    @Override
    public int compare(WebApplicationContextListener o1, WebApplicationContextListener o2) {
        Priority priorityOne = o1.getClass().getAnnotation(Priority.class);
        Priority priorityTwo = o2.getClass().getAnnotation(Priority.class);
        int valueOne = null == priorityOne ? 0 : priorityOne.value();
        int valueTwo = null == priorityTwo ? 0 : priorityTwo.value();
        return Integer.compare(valueOne, valueTwo);
    }
}
