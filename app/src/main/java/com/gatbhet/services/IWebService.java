package com.gatbhet.services;

import java.util.HashMap;

/**
 * Created by a514192 on 10-04-2016.
 */
public interface IWebService {
    public HashMap<String, String> getHeader();
    public String getMethodType();
    public String getBody();
    public String getURLParams();
    public String getKeyValueQueryParam();
}
