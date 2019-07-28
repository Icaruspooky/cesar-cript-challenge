package br.com.cesar.cript.cesar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceivedMessage {


    private int numero_casas;
    private String token;
    private String cifrado;
    private String decifrado;
    private String resumo_criptografico;


    public String decode() {
       int[] encodedMessage = cifrado.chars().toArray();
        StringBuilder builder = new StringBuilder();

        for (int letter : encodedMessage) {
            if (letter >= 97 && letter <= 122) {
                builder.append((char) (((letter - 97 + 26 - numero_casas) % 26) + 97));
            } else {
                builder.append((char) letter);
            }
        }

        return builder.toString();
    }
}
