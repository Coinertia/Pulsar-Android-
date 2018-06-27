package com.pulsar.android.Models;

import com.pulsar.android.GlobalVar;

public class HistoryItem {

    String strReceipt;
    String strSender;
    String strId;
    String strAsset;
    String strFeeAsset;
    long nTime;
    long nFee;
    long nAmount;
    String strDesc;
    boolean isUnconfirmed = false, isFirst = false;
    public HistoryItem(String strReceipt, String strSender, String strId, String strAsset, String strFee, String strDesc, long nTime, long nAmount, long nFee, boolean isUnconfirmed) {
        this.strReceipt = strReceipt;
        this.strSender = strSender;
        this.strId = strId;
        this.strAsset = strAsset;
        this.strFeeAsset = strFee;
        this.strDesc = strDesc;
        this.nTime = nTime;
        this.nAmount = nAmount;
        this.nFee = nFee;
        this.isUnconfirmed = isUnconfirmed;
    }


    public String getStrReceipt() {
//        if(getIsSender() == 3){
//            return strSender;
//        }else{
            return strReceipt;
//        }
    }
    public String getStrSender() {
        return strSender;
    }
    public String getStrId() {
        return strId;
    }
    public String getStrAsset() {
        return strAsset;
    }
    public String getStrFeeAsset(){
        return strFeeAsset;
    }
    public String getStrDesc(){
        return strDesc;
    }
    public long getnTime(){
        return nTime;
    }
    public String getStrAmount(){
        return String.format("%.5f", (double)nAmount/100000000);
    }
    public String getStrFee(){
        return String.format("%.5f", (double)nFee/100000000);
    }
    public int getIsSender(){
        if(strSender.equals(GlobalVar.strAddress)){
            return 2;
        }else{
            return 3;
        }
    }
    public int getCardId(){
        for(int i =0; i < GlobalVar.assetID.length; i ++){
            if(GlobalVar.assetID[i].equals(strAsset))
                return i;
        }
        return  0;
    }
    public int getFeeId(){
        for(int i =0; i < GlobalVar.assetID.length; i ++){
            if(GlobalVar.assetID[i].equals(strFeeAsset))
                return i;
        }
        return  0;
    }
    public void setFirst(boolean isFirst){
        this.isFirst= isFirst;
    }
    public boolean getFirst(){
        return this.isFirst;
    }
    public boolean getUnconfirmed(){
        return this.isUnconfirmed;
    }
}