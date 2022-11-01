package com.turtleshelldevelopment.utils.db;

import java.util.Date;

public record Vaccine(int lotNumber, int site_id, int patient_id, Date administeredDate, String manufacturer, int dose,
                      int administeredBy, ProvidingUser givenBy, Site site) {
}
