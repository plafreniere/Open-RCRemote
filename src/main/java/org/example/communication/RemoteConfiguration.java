package org.example.communication;

import lombok.Data;

import java.io.Serializable;

@Data
public class RemoteConfiguration implements Serializable {
    private String ip;
    private String port;
    private RemoteType type;
    private boolean videoCompatible;

}
