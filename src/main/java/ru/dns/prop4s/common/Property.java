package ru.dns.prop4s.common;

import ru.dns.prop4s.ManagerProperty;

import java.io.IOException;

public class Property<P> {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ManagerProperty.class);

    private final P property;
    private ILoader loader;
    private boolean needUpdated;
    private String checkSum;

    public Property(final Class<P> pClass, ILoader loader) {
        super();
        this.loader = loader;
        try {
            this.property = pClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        update();
    }

    public Property(final Class<P> pClass) throws IOException {
        this(pClass, null);
    }

    public P getProperty() {
        return property;
    }

    public ILoader getLoader() {
        return loader;
    }

    public boolean update() {
        if (loader == null) {
            return false;
        }
        boolean b = false;
        try {
            b = loader.update(this);
            needUpdated = false;
        } catch (IOException e) {
            logger.error("error update", e);
        }
        return b;
    }

    public boolean isNeedUpdated() {
        if (loader == null) {
            return false;
        }
        try {
            needUpdated = needUpdated || loader.isChanged(this);
        } catch (IOException e) {
            logger.error("Error check need update", e);
        }
        return needUpdated;
    }

    public void setNeedUpdated(boolean needUpdated) {
        this.needUpdated = needUpdated;
    }

    String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    @Override
    public String toString() {
        String s = property.toString();
        return s.replace(",", "\n").
                replace("{", "{\n ").
                replace("}", "\n}");
    }

}
