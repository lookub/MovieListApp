package com.uakgul.moviedb.moviet.presenter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.uakgul.moviedb.moviet.model.Cast;
import com.uakgul.moviedb.moviet.model.Credits;
import com.uakgul.moviedb.moviet.model.Crew;

import java.util.List;

public class CreditsResponse {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("cast")
    @Expose
    private List<Cast> cast = null;

    @SerializedName("crew")
    @Expose
    private List<Crew> crew = null;

    @Override
    public String toString() {
        return "CreditsResponse{" +
                "id=" + id +
                ", cast=" + cast +
                ", crew=" + crew +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Cast> getCast() {
        return cast;
    }

    public void setCast(List<Cast> cast) {
        this.cast = cast;
    }

    public List<Crew> getCrew() {
        return crew;
    }

    public void setCrew(List<Crew> crew) {
        this.crew = crew;
    }

}
