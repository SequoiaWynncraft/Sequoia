package star.sequoia2.client.services.mojang;

import java.util.Objects;

public class MojangUsersProfilesMinecraftResponse {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MojangUsersProfilesMinecraftResponse that = (MojangUsersProfilesMinecraftResponse) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "MojangUsersProfilesMinecraftResponse{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
    }
}