package com.udipoc.api.dto.response;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.List;

@Data
public class ResidentInfoResponse implements Serializable {
    private static final long serialVersionUID = 8907302024886152312L;

    private String status;
    private List errors;
    @ApiModelProperty(name = "identityObject", dataType = "List", example = "{additionalProp1: []," + "additionalProp2: []," + "additionalProp3: []}")
    private JSONObject identityObject;
    private List documentsArray;
}
