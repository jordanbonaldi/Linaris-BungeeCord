package net.neferett.linaris.utils.password;

import org.apache.commons.lang3.ObjectUtils;

public enum HashAlgorithm
{
    SHA256((Class<?>)SHA256.class),  
    CUSTOM((Class<?>)ObjectUtils.Null.class);
	
    
    Class<?> classe;
    
    private HashAlgorithm(final Class<?> classe) {
        this.classe = classe;
    }
    
    public Class<?> getclass() {
        return this.classe;
    }
}
