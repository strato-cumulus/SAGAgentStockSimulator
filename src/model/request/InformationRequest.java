package model.request;

import model.messagecontent.Information;

import java.util.List;

public class InformationRequest {

    public final List<Information> information;

    public InformationRequest(List<Information> information) {
        this.information = information;
    }
}
