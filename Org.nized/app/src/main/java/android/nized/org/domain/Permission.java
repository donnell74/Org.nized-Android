package android.nized.org.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * Created by greg on 5/2/15.
 */
@JsonSerialize
@JsonDeserialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Permission implements Serializable {
    private int id;
    private int role_id;
    private String model;
    private boolean self;
    private boolean other;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean getSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }

    public boolean getOther() {
        return other;
    }

    public void setOther(boolean other) {
        this.other = other;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        if (id != that.id) return false;
        if (role_id != that.role_id) return false;
        if (self != that.self) return false;
        if (other != that.other) return false;
        return !(model != null ? !model.equals(that.model) : that.model != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + role_id;
        result = 31 * result + (model != null ? model.hashCode() : 0);
        result = 31 * result + (self ? 1 : 0);
        result = 31 * result + (other ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", role_id=" + role_id +
                ", model='" + model + '\'' +
                ", self=" + self +
                ", other=" + other +
                '}';
    }
}
