package com.xarhabia.BankApp.utils.log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestResponseLog {

    public static String logRequestTransaction(Object request) {
        StringBuilder logText = new StringBuilder();
        logText.append("\n============== INICIO DE LA TRANSACCION ==============\n");
        logText.append("Request: ").append(request).append("\n");
        logText.append("---------------------------------------------");
        return logText.toString();
    }

    public static String logResponseTransaction(Object response) {
        StringBuilder logText = new StringBuilder();
        logText.append("\n---------------------------------------------\n");
        logText.append("Response: ").append(response).append("\n");
        logText.append("=============== FIN DE LA TRANSACCION ===============\n");
        return logText.toString();
    }

    public static void writeLog(String logs) {
        log.info(logs);
    }
}
