package com.poketrainer.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PokeApiPokemonResponse {

    private Long id;
    private String name;
    private Integer height;
    private Integer weight;
    private List<PokemonTypeSlot> types;
    private List<PokemonAbilitySlot> abilities;
    private List<PokemonStatSlot> stats;
    private List<PokemonMoveSlot> moves;
    private Sprites sprites;

    public PokeApiPokemonResponse() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWeight() {
        return weight;
    }

    public List<PokemonTypeSlot> getTypes() {
        return types;
    }

    public List<PokemonAbilitySlot> getAbilities() {
        return abilities;
    }

    public List<PokemonStatSlot> getStats() {
        return stats;
    }

    public List<PokemonMoveSlot> getMoves() {
        return moves;
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

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setTypes(List<PokemonTypeSlot> types) {
        this.types = types;
    }

    public void setAbilities(List<PokemonAbilitySlot> abilities) {
        this.abilities = abilities;
    }

    public void setStats(List<PokemonStatSlot> stats) {
        this.stats = stats;
    }

    public void setMoves(List<PokemonMoveSlot> moves) {
        this.moves = moves;
    }

    public void setSprites(Sprites sprites) {
        this.sprites = sprites;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonAbilitySlot {
        private boolean is_hidden;
        private int slot;
        private NamedResource ability;

        public PokemonAbilitySlot() {
        }

        public boolean isIs_hidden() {
            return is_hidden;
        }

        public int getSlot() {
            return slot;
        }

        public NamedResource getAbility() {
            return ability;
        }

        public void setIs_hidden(boolean is_hidden) {
            this.is_hidden = is_hidden;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        public void setAbility(NamedResource ability) {
            this.ability = ability;
        }
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
    public static class PokemonMoveSlot {
        private NamedResource move;
        private List<VersionGroupDetail> version_group_details;

        public PokemonMoveSlot() {
        }

        public NamedResource getMove() {
            return move;
        }

        public List<VersionGroupDetail> getVersion_group_details() {
            return version_group_details;
        }

        public void setMove(NamedResource move) {
            this.move = move;
        }

        public void setVersion_group_details(List<VersionGroupDetail> version_group_details) {
            this.version_group_details = version_group_details;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VersionGroupDetail {
        private NamedResource move_learn_method;

        public VersionGroupDetail() {
        }

        public NamedResource getMove_learn_method() {
            return move_learn_method;
        }

        public void setMove_learn_method(NamedResource move_learn_method) {
            this.move_learn_method = move_learn_method;
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
