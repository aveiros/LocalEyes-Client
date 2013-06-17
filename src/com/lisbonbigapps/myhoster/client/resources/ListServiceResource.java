package com.lisbonbigapps.myhoster.client.resources;

import java.util.ArrayList;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListServiceResource extends ArrayList<ServiceResource> {
}