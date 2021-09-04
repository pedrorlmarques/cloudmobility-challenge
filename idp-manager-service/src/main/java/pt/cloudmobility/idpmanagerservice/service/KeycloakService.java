package pt.cloudmobility.idpmanagerservice.service;

import pt.cloudmobility.idpmanagerservice.dto.KeycloakUserInputRequest;

public interface KeycloakService {

    void execute(KeycloakUserInputRequest userInputRequest);
}
