package com.me.utils;

import com.me.calculator.TransactionRecord;
import com.me.calculator.TransactionType;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Stream;

public class TransactionUtils {

    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public static NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

    private static int TRANSACTION_TYPE_FIELD_IDX = 5;

    public static boolean isTrxCredit(String accountId, TransactionRecord trxRecord) {
        if ( accountId.equalsIgnoreCase(trxRecord.getToAccountId()) &&
                trxRecord.getTransactionType().equals(TransactionType.PAYMENT))
            return true;

        return false;
    }

    public static boolean isTrxCreditReversal(String accountId, TransactionRecord trxRecord, List<String> balanceTrx) {
        if ( accountId.equalsIgnoreCase(trxRecord.getToAccountId()) &&
                trxRecord.getTransactionType().equals(TransactionType.REVERSAL) &&
                balanceTrx.contains(trxRecord.getRelatedTransaction()) )
            return true;

        return false;
    }

    public static boolean isTrxDebit(String accountId, TransactionRecord trxRecord) {
        if ( accountId.equalsIgnoreCase(trxRecord.getFromAccountId()) &&
                trxRecord.getTransactionType().equals(TransactionType.PAYMENT))
            return true;

        return false;
    }

    public static boolean isTrxDebitReversal(String accountId, TransactionRecord trxRecord, List<String> balanceTrx) {
        if ( accountId.equalsIgnoreCase(trxRecord.getFromAccountId()) &&
                trxRecord.getTransactionType().equals(TransactionType.REVERSAL) &&
                balanceTrx.contains(trxRecord.getRelatedTransaction()))
            return true;

        return false;
    }

    public static boolean isTimeValidTrx(LocalDateTime startTime, LocalDateTime endTime, TransactionRecord trxRecord){
        if ((trxRecord.getCreateAt().isAfter(startTime) && trxRecord.getCreateAt().isBefore(endTime))
            || trxRecord.getCreateAt().isEqual(startTime)
            || trxRecord.getCreateAt().isEqual(endTime))
            return true;

        return false;
    }

    public static String[] validTrxFormat(String trxRecord) {
        String[] fields = trimFields(trxRecord);
        int len = fields.length;

        if (!(len > TRANSACTION_TYPE_FIELD_IDX &&
            TransactionType.enumValuesAsList().contains(fields[TRANSACTION_TYPE_FIELD_IDX])))
             return null;

        if (fields[0].isEmpty() ||
            fields[1].isEmpty() ||
            fields[2].isEmpty() ||
            fields[3].isEmpty() ||
            fields[4].isEmpty() ||
            fields[5].isEmpty()
        )
            return null;

        try {
            Double.parseDouble(fields[4]);
        } catch (NumberFormatException e) {
            return null;
        }

        try{
            LocalDateTime.parse(fields[3], TransactionUtils.dateTimeFormatter);
        }
        catch (DateTimeParseException e) {
            return null;
        }

        return fields;
    }

    private static String[] trimFields(String record){
        return Stream.of(record.split(",")).map(String::trim).toArray(String[]::new);
    }

}
