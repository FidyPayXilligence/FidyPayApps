package com.fidypay.request;

public class FloatRequest {

	    private String floatTo;
		private double	amount;
		private double	receivedAmt;
		private String	payMode;
        private String description;
        private String  txnType;
        private Long merchantId;
		private char isReverted;
		private long merchantTillId;
	
		public String getFloatTo() {
			return floatTo;
		}
		public void setFloatTo(String floatTo) {
			this.floatTo = floatTo;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
		public double getReceivedAmt() {
			return receivedAmt;
		}
		public void setReceivedAmt(double receivedAmt) {
			this.receivedAmt = receivedAmt;
		}
		public String getPayMode() {
			return payMode;
		}
		public void setPayMode(String payMode) {
			this.payMode = payMode;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getTxnType() {
			return txnType;
		}
		public void setTxnType(String txnType) {
			this.txnType = txnType;
		}
		public Long getMerchantId() {
			return merchantId;
		}
		public void setMerchantId(Long merchantId) {
			this.merchantId = merchantId;
		}
		public char getIsReverted() {
			return isReverted;
		}
		public void setIsReverted(char isReverted) {
			this.isReverted = isReverted;
		}
		public long getMerchantTillId() {
			return merchantTillId;
		}
		public void setMerchantTillId(long merchantTillId) {
			this.merchantTillId = merchantTillId;
		}
	
		
		
		
	
}
