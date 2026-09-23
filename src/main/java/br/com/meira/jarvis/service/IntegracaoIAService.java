package br.com.meira.jarvis.service;

import  org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


@Service 
public class IntegracaoIAService {
    private final String apiKey;

    public IntegracaoIAService(@Value("${openai.api-key}") String apikey){
        this.apiKey = apiKey;
    }
}
