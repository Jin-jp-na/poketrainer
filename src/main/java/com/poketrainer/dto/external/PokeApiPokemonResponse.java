package com.poketrainer.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PokeApiPokemonResponse {

    private Long id;
    private String name;
    private List<PokemonTypeSlot> types;
    private List<PokemonStatSlot> stats;
    private Sprites sprites;

    public PokeApiPokemonResponse() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<PokemonTypeSlot> getTypes() {
        return types;
    }

    public List<PokemonStatSlot> getStats() {
        return stats;
    }

    public Sprites getSprites() {
        return sprites;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypes(List<PokemonTypeSlot> types) {
        this.types = types;
    }

    public void setStats(List<PokemonStatSlot> stats) {
        this.stats = stats;
    }

    public void setSprites(Sprites sprites) {
        this.sprites = sprites;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonTypeSlot {
        private int slot;
        private NamedResource type;

        public PokemonTypeSlot() {
        }

        public int getSlot() {
            return slot;
        }

        public NamedResource getType() {
            return type;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        public void setType(NamedResource type) {
            this.type = type;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonStatSlot {
        private int base_stat;
        private NamedResource stat;

        public PokemonStatSlot() {
        }

        public int getBase_stat() {
            return base_stat;
        }

        public NamedResource getStat() {
            return stat;
        }

        public void setBase_stat(int base_stat) {
            this.base_stat = base_stat;
        }

        public void setStat(NamedResource stat) {
            this.stat = stat;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NamedResource {
        private String name;
        private String url;

        public NamedResource() {
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sprites {
        private String front_default;

        public Sprites() {
        }

        public String getFront_default() {
            return front_default;
        }

        public void setFront_default(String front_default) {
            this.front_default = front_default;
        }
    }
}