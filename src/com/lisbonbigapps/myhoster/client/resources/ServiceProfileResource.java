package com.lisbonbigapps.myhoster.client.resources;

public class ServiceProfileResource extends RootResource {
    String description;
    float fee;
    float rate;
    long votes;

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public float getFee() {
	return fee;
    }

    public void setFee(float fee) {
	this.fee = fee;
    }

    public long getVotes() {
	return votes;
    }

    public void setVotes(long votes) {
	this.votes = votes;
    }

    public float getRate() {
	return rate;
    }

    public void setRate(float rate) {
	this.rate = rate;
    }
}
