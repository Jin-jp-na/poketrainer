(function () {
    var API = window.location.protocol === 'file:' ? 'http://localhost:8080' : '';
    var POKE_API = 'https://pokeapi.co/api/v2';
    var BLANK_SPRITE = 'data:image/gif;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs=';
    var allPokemon = [];
    var pokemonMoveCache = {};
    var moveTypeLookupPromise = null;
    var activeMode = 'defense';
    var pickerSlot = null;
    var movePickerSlot = null;
    var movePickerIndex = null;
    var busy = false;
    var ALL_TYPES = ['normal', 'fire', 'water', 'electric', 'grass', 'ice', 'fighting', 'poison', 'ground', 'flying', 'psychic', 'bug', 'rock', 'ghost', 'dragon', 'dark', 'steel', 'fairy'];
    var CHART = {
        normal: { rock: 0.5, ghost: 0, steel: 0.5 },
        fire: { fire: 0.5, water: 0.5, grass: 2, ice: 2, bug: 2, rock: 0.5, dragon: 0.5, steel: 2 },
        water: { fire: 2, water: 0.5, grass: 0.5, ground: 2, rock: 2, dragon: 0.5 },
        electric: { water: 2, electric: 0.5, grass: 0.5, ground: 0, flying: 2, dragon: 0.5 },
        grass: { fire: 0.5, water: 2, grass: 0.5, poison: 0.5, ground: 2, flying: 0.5, bug: 0.5, rock: 2, dragon: 0.5, steel: 0.5 },
        ice: { fire: 0.5, water: 0.5, grass: 2, ice: 0.5, ground: 2, flying: 2, dragon: 2, steel: 0.5 },
        fighting: { normal: 2, ice: 2, poison: 0.5, flying: 0.5, psychic: 0.5, bug: 0.5, rock: 2, ghost: 0, dark: 2, steel: 2, fairy: 0.5 },
        poison: { grass: 2, poison: 0.5, ground: 0.5, rock: 0.5, ghost: 0.5, steel: 0, fairy: 2 },
        ground: { fire: 2, electric: 2, grass: 0.5, poison: 2, flying: 0, bug: 0.5, rock: 2, steel: 2 },
        flying: { electric: 0.5, grass: 2, fighting: 2, bug: 2, rock: 0.5, steel: 0.5 },
        psychic: { fighting: 2, poison: 2, psychic: 0.5, dark: 0, steel: 0.5 },
        bug: { fire: 0.5, grass: 2, fighting: 0.5, flying: 0.5, psychic: 2, ghost: 0.5, dark: 2, steel: 0.5, fairy: 0.5 },
        rock: { fire: 2, ice: 2, fighting: 0.5, ground: 0.5, flying: 2, bug: 2, steel: 0.5 },
        ghost: { normal: 0, fighting: 0, poison: 0.5, bug: 0.5, ghost: 2, dark: 0.5 },
        dragon: { dragon: 2, steel: 0.5, fairy: 0 },
        dark: { fighting: 0.5, psychic: 2, ghost: 2, dark: 0.5, fairy: 0.5 },
        steel: { normal: 0.5, fire: 0.5, water: 0.5, electric: 0.5, ice: 2, fighting: 2, poison: 0, ground: 2, flying: 0.5, psychic: 0.5, bug: 0.5, rock: 2, dragon: 0.5, steel: 0.5, fairy: 2 },
        fairy: { fire: 0.5, fighting: 2, poison: 0.5, dragon: 2, dark: 2, steel: 0.5 }
    };
    var team = [];
    var i;

    for (i = 0; i < 6; i++) {
        team.push({ pokemon: null, moves: [null, null, null, null] });
    }

    function spriteStatic(pokemon) {
        return pokemon.spriteUrl || BLANK_SPRITE;
    }

    function moveType(move) {
        return move && move.type ? String(move.type).toLowerCase() : '';
    }

    function moveTypeText(type) {
        return type ? type.slice(0, 3).toUpperCase() : '---';
    }

    function moveTypeChip(type, className) {
        var safeType = type || 'empty';
        return '<span class="' + className + '">' +
            '<span class="move-type ' + safeType + '"></span>' +
            '<span class="move-type-label">' + moveTypeText(type) + '</span>' +
        '</span>';
    }

    function setStatus(message, isError) {
        var status = document.getElementById('status');
        status.textContent = message;
        status.classList.toggle('error', Boolean(isError));
    }

    function setBusy(nextBusy) {
        busy = nextBusy;
        document.getElementById('auto-team-btn').disabled = nextBusy || !allPokemon.length;
        document.querySelectorAll('.moves-auto').forEach(function (button) {
            button.disabled = nextBusy;
        });
    }

    function effect(atk, type1, type2) {
        var row = CHART[atk] || {};
        var first = row[type1] !== undefined ? row[type1] : 1;
        var second = type2 && type2 !== type1 ? (row[type2] !== undefined ? row[type2] : 1) : 1;
        return first * second;
    }

    function filledTeam() {
        return team.filter(function (slot) {
            return slot.pokemon;
        });
    }

    function renderSlots() {
        var container = document.getElementById('slots');
        container.innerHTML = '';
        for (var index = 0; index < 6; index++) {
            container.appendChild(buildSlot(index));
        }
    }

    function buildSlot(index) {
        var slot = team[index];
        var wrapper = document.createElement('div');
        wrapper.className = 'slot';

        if (!slot.pokemon) {
            wrapper.innerHTML = '<div class="slot-empty" data-slot="' + index + '">+ ADD TO SLOT ' + (index + 1) + '</div>';
            wrapper.querySelector('.slot-empty').onclick = function () {
                openPokemonPicker(index);
            };
            return wrapper;
        }

        var movesHtml = '';
        slot.moves.forEach(function (move, moveIndex) {
            var typeClass = moveType(move);
            var name = move ? move.name.replace(/-/g, ' ').toUpperCase() : 'MOVE ' + (moveIndex + 1);
            movesHtml += '<div class="move-slot" data-slot="' + index + '" data-move="' + moveIndex + '">' +
                moveTypeChip(typeClass, 'move-type-chip') +
                '<span class="move-name">' + name + '</span>' +
            '</div>';
        });

        wrapper.innerHTML =
            '<div class="slot-head">' +
                '<img class="slot-sprite" src="' + spriteStatic(slot.pokemon) + '" alt="' + slot.pokemon.name + '">' +
                '<span class="slot-name">' + slot.pokemon.name.toUpperCase() + '</span>' +
                '<button class="moves-auto" data-slot="' + index + '">MOVES</button>' +
                '<button class="slot-remove" data-slot="' + index + '">X</button>' +
            '</div>' +
            '<div class="move-list">' + movesHtml + '</div>';

        wrapper.querySelector('.slot-remove').onclick = function () {
            team[index] = { pokemon: null, moves: [null, null, null, null] };
            renderSlots();
            renderCoverage();
        };

        wrapper.querySelectorAll('.move-slot').forEach(function (moveSlot) {
            moveSlot.onclick = function () {
                openMovePicker(Number(moveSlot.dataset.slot), Number(moveSlot.dataset.move));
            };
        });

        return wrapper;
    }

    function openPokemonPicker(index) {
        if (!allPokemon.length) {
            setStatus('POKEMON DATA IS NOT READY', true);
            return;
        }
        pickerSlot = index;
        document.getElementById('pokemon-search').value = '';
        renderPokemonPicker('');
        document.getElementById('pokemon-overlay').style.display = 'flex';
    }

    function closePokemonPicker() {
        document.getElementById('pokemon-overlay').style.display = 'none';
        pickerSlot = null;
    }

    function renderPokemonPicker(query) {
        var list = document.getElementById('pokemon-picker-list');
        var normalized = query.toLowerCase();
        var subset = allPokemon.filter(function (pokemon) {
            return !normalized || pokemon.name.toLowerCase().indexOf(normalized) !== -1;
        }).slice(0, 120);

        list.innerHTML = '';
        if (!subset.length) {
            list.innerHTML = '<div class="status-box">NO MATCHES</div>';
            return;
        }

        var fragment = document.createDocumentFragment();
        subset.forEach(function (pokemon) {
            var item = document.createElement('div');
            item.className = 'picker-item';
            item.innerHTML =
                '<img class="picker-sprite" src="' + spriteStatic(pokemon) + '" alt="' + pokemon.name + '">' +
                '<span class="move-name">' + pokemon.name.toUpperCase() + '</span>';
            item.onclick = function () {
                team[pickerSlot] = { pokemon: pokemon, moves: [null, null, null, null] };
                closePokemonPicker();
                renderSlots();
                renderCoverage();
            };
            fragment.appendChild(item);
        });
        list.appendChild(fragment);
    }

    function openMovePicker(slotIndex, moveIndex) {
        var slot = team[slotIndex];
        if (!slot.pokemon) {
            return;
        }
        movePickerSlot = slotIndex;
        movePickerIndex = moveIndex;
        document.getElementById('move-search').value = '';
        document.getElementById('move-picker-list').innerHTML = '<div class="status-box">LOADING...</div>';
        document.getElementById('move-overlay').style.display = 'flex';

        loadPokemonMoves(slot.pokemon.id).then(function (moves) {
            renderMovePicker(moves || [], '');
        }).catch(function () {
            document.getElementById('move-picker-list').innerHTML = '<div class="status-box">COULD NOT LOAD MOVES</div>';
        });
    }

    function closeMovePicker() {
        document.getElementById('move-overlay').style.display = 'none';
        movePickerSlot = null;
        movePickerIndex = null;
    }

    function renderMovePicker(moves, query) {
        var list = document.getElementById('move-picker-list');
        var seen = {};
        var normalized = query.toLowerCase();
        var uniqueMoves = moves.filter(function (move) {
            if (seen[move.name]) {
                return false;
            }
            seen[move.name] = true;
            return true;
        }).filter(function (move) {
            return !normalized || move.name.toLowerCase().indexOf(normalized) !== -1;
        }).slice(0, 160);

        list.innerHTML = '';
        if (!uniqueMoves.length) {
            list.innerHTML = '<div class="status-box">NO MOVES FOUND</div>';
            return;
        }

        var fragment = document.createDocumentFragment();
        uniqueMoves.forEach(function (move) {
            var item = document.createElement('div');
            item.className = 'picker-item';
            var type = moveType(move);
            item.innerHTML =
                moveTypeChip(type, 'move-type-chip picker-type-chip') +
                '<span class="move-name">' + move.name.replace(/-/g, ' ').toUpperCase() + '</span>';
            item.onclick = function () {
                applyMoveSelection(move);
            };
            fragment.appendChild(item);
        });
        list.appendChild(fragment);
    }

    function applyMoveSelection(move) {
        team[movePickerSlot].moves[movePickerIndex] = {
            name: move.name,
            type: moveType(move),
            powerValue: move.powerValue || 0,
            accuracy: move.accuracy || 100
        };
        closeMovePicker();
        renderSlots();
        renderCoverage();
    }

    function renderCoverage() {
        if (activeMode === 'defense') {
            renderDefense();
        } else {
            renderOffense();
        }
    }

    function renderDefense() {
        var content = document.getElementById('coverage-content');
        var roster = filledTeam();
        if (!roster.length) {
            content.innerHTML = '<div class="status-box">ADD POKEMON TO SEE COVERAGE</div>';
            return;
        }

        var html = '<table><thead><tr><th>ATK</th>';
        roster.forEach(function (slot) {
            html += '<th><img class="cover-sprite" src="' + spriteStatic(slot.pokemon) + '" alt="' + slot.pokemon.name + '"></th>';
        });
        html += '<th>TEAM</th></tr></thead><tbody>';

        ALL_TYPES.forEach(function (attackingType) {
            html += '<tr><td>' + attackingType.slice(0, 3).toUpperCase() + '</td>';
            var weak = 0;
            var resist = 0;
            var immune = 0;
            roster.forEach(function (slot) {
                var t1 = (slot.pokemon.primaryType || '').toLowerCase();
                var t2 = (slot.pokemon.secondaryType || '').toLowerCase();
                var multiplier = effect(attackingType, t1, t2);
                var className = '';
                var label = '-';
                if (multiplier === 0) {
                    className = 'immune';
                    label = '0';
                    immune++;
                } else if (multiplier <= 0.25) {
                    className = 'resist4';
                    label = '1/4';
                    resist++;
                } else if (multiplier < 1) {
                    className = 'resist2';
                    label = '1/2';
                    resist++;
                } else if (multiplier >= 4) {
                    className = 'weak4';
                    label = '4x';
                    weak++;
                } else if (multiplier > 1) {
                    className = 'weak2';
                    label = '2x';
                    weak++;
                }
                html += '<td class="' + className + '">' + label + '</td>';
            });
            html += '<td>' + weak + 'W / ' + resist + 'R / ' + immune + 'I</td></tr>';
        });

        html += '</tbody></table>';
        content.innerHTML = html;
    }

    function renderOffense() {
        var content = document.getElementById('coverage-content');
        var roster = filledTeam();
        if (!roster.length) {
            content.innerHTML = '<div class="status-box">ADD POKEMON TO SEE COVERAGE</div>';
            return;
        }

        var hasTypedMoves = roster.some(function (slot) {
            return slot.moves.some(function (move) { return move && moveType(move); });
        });

        var html = hasTypedMoves ? '' : '<div class="coverage-note">ADD TYPED MOVES TO CALCULATE OFFENSIVE COVERAGE</div>';
        html += '<table><thead><tr><th>VS</th>';
        roster.forEach(function (slot) {
            html += '<th><img class="cover-sprite" src="' + spriteStatic(slot.pokemon) + '" alt="' + slot.pokemon.name + '"></th>';
        });
        html += '<th>CVR</th></tr></thead><tbody>';

        ALL_TYPES.forEach(function (defendingType) {
            html += '<tr><td>' + defendingType.slice(0, 3).toUpperCase() + '</td>';
            var count = 0;
            roster.forEach(function (slot) {
                var best = 0;
                slot.moves.forEach(function (move) {
                    var type = moveType(move);
                    if (!type) {
                        return;
                    }
                    var value = (CHART[type] && CHART[type][defendingType] !== undefined) ? CHART[type][defendingType] : 1;
                    if (value > best) {
                        best = value;
                    }
                });
                var className = '';
                var label = '-';
                if (best >= 4) {
                    className = 'weak4';
                    label = '4x';
                    count++;
                } else if (best >= 2) {
                    className = 'weak2';
                    label = '2x';
                    count++;
                } else if (best > 0 && best < 1) {
                    className = 'resist2';
                } else if (best === 0) {
                    className = 'immune';
                }
                html += '<td class="' + className + '">' + label + '</td>';
            });
            html += '<td>' + count + '</td></tr>';
        });

        html += '</tbody></table>';
        content.innerHTML = html;
    }

    function scoreCandidate(candidate) {
        var score = ((candidate.hp || 0) + (candidate.attackStat || 0) + (candidate.defenseStat || 0) + (candidate.speedStat || 0)) * 0.008;
        ALL_TYPES.forEach(function (attackingType) {
            var weakCount = 0;
            team.forEach(function (slot) {
                if (!slot.pokemon) {
                    return;
                }
                var t1 = (slot.pokemon.primaryType || '').toLowerCase();
                var t2 = (slot.pokemon.secondaryType || '').toLowerCase();
                if (effect(attackingType, t1, t2) >= 2) {
                    weakCount++;
                }
            });
            if (!weakCount) {
                return;
            }
            var p1 = (candidate.primaryType || '').toLowerCase();
            var p2 = (candidate.secondaryType || '').toLowerCase();
            var candidateEffect = effect(attackingType, p1, p2);
            if (candidateEffect === 0) {
                score += weakCount * 4;
            } else if (candidateEffect < 1) {
                score += weakCount * 2;
            } else if (candidateEffect >= 2) {
                score -= weakCount * 3;
            }
        });
        team.forEach(function (slot) {
            if (!slot.pokemon) {
                return;
            }
            var slotPrimary = (slot.pokemon.primaryType || '').toLowerCase();
            var slotSecondary = (slot.pokemon.secondaryType || '').toLowerCase();
            var candidatePrimary = (candidate.primaryType || '').toLowerCase();
            var candidateSecondary = (candidate.secondaryType || '').toLowerCase();
            if (slotPrimary === candidatePrimary || (slotSecondary && slotSecondary === candidatePrimary)) {
                score -= 6;
            }
            if (candidateSecondary && (slotPrimary === candidateSecondary || (slotSecondary && slotSecondary === candidateSecondary))) {
                score -= 3;
            }
        });
        return score;
    }

    function loadPokemonMoves(id) {
        if (pokemonMoveCache[id]) {
            return Promise.resolve(pokemonMoveCache[id]);
        }
        return fetchWithTimeout(API + '/api/pokemon/' + id + '/moves', 5000).then(function (response) {
            if (!response.ok) {
                throw new Error('HTTP ' + response.status);
            }
            return response.json();
        }).then(function (moves) {
            return hydrateMoveTypes(moves || []);
        }).catch(function () {
            return loadPokemonMovesFromPokeApi(id);
        }).then(function (moves) {
            pokemonMoveCache[id] = moves;
            return moves;
        });
    }

    function fetchWithTimeout(url, timeoutMs) {
        return new Promise(function (resolve, reject) {
            var finished = false;
            var timer = setTimeout(function () {
                if (!finished) {
                    finished = true;
                    reject(new Error('Timed out loading ' + url));
                }
            }, timeoutMs);

            fetch(url).then(function (response) {
                if (!finished) {
                    finished = true;
                    clearTimeout(timer);
                    resolve(response);
                }
            }).catch(function (error) {
                if (!finished) {
                    finished = true;
                    clearTimeout(timer);
                    reject(error);
                }
            });
        });
    }

    function loadPokemonMovesFromPokeApi(id) {
        return fetch(POKE_API + '/pokemon/' + id + '/').then(function (response) {
            if (!response.ok) {
                throw new Error('HTTP ' + response.status);
            }
            return response.json();
        }).then(function (data) {
            var seen = {};
            var moves = (data.moves || []).filter(function (slot) {
                if (!slot.move || !slot.move.name || seen[slot.move.name]) {
                    return false;
                }
                seen[slot.move.name] = true;
                return true;
            }).map(function (slot) {
                return {
                    name: slot.move.name,
                    moveUrl: slot.move.url,
                    learnMethod: resolveLearnMethod(slot)
                };
            });
            return hydrateMoveTypes(moves);
        });
    }

    function resolveLearnMethod(slot) {
        var detail = (slot.version_group_details || [])[0];
        return detail && detail.move_learn_method ? detail.move_learn_method.name : '';
    }

    function hydrateMoveTypes(moves) {
        var normalizedMoves = (moves || []).map(normalizeMove);
        if (normalizedMoves.every(function (move) { return moveType(move); })) {
            return Promise.resolve(normalizedMoves);
        }

        return fetchMoveTypeLookup().then(function (lookup) {
            return normalizedMoves.map(function (move) {
                if (!move.type && lookup[move.name]) {
                    move.type = lookup[move.name];
                }
                return move;
            });
        }).catch(function () {
            return normalizedMoves;
        });
    }

    function fetchMoveTypeLookup() {
        if (!moveTypeLookupPromise) {
            moveTypeLookupPromise = Promise.all(ALL_TYPES.map(function (type) {
                return fetch(POKE_API + '/type/' + type + '/').then(function (response) {
                    if (!response.ok) {
                        throw new Error('HTTP ' + response.status);
                    }
                    return response.json();
                }).then(function (data) {
                    return { type: type, moves: data.moves || [] };
                });
            })).then(function (typeGroups) {
                var lookup = {};
                typeGroups.forEach(function (group) {
                    group.moves.forEach(function (move) {
                        var name = readMoveNameFromTypeSlot(move);
                        if (name && !lookup[name]) {
                            lookup[name] = group.type;
                        }
                    });
                });
                return lookup;
            }).catch(function (error) {
                moveTypeLookupPromise = null;
                throw error;
            });
        }
        return moveTypeLookupPromise;
    }

    function readMoveNameFromTypeSlot(move) {
        if (move && move.name) {
            return move.name;
        }
        if (move && move.move && move.move.name) {
            return move.move.name;
        }
        return '';
    }

    function normalizeMove(move) {
        return {
            name: move.name,
            type: moveType(move),
            powerValue: move.powerValue == null ? 0 : move.powerValue,
            accuracy: move.accuracy == null ? 100 : move.accuracy,
            learnMethod: move.learnMethod || ''
        };
    }

    function scoreMove(moveDetail, pokemon) {
        var score = (moveDetail.powerValue || 0) * ((moveDetail.accuracy || 100) / 100);
        if (!score) {
            score = 5;
        }
        var t1 = (pokemon.primaryType || '').toLowerCase();
        var t2 = (pokemon.secondaryType || '').toLowerCase();
        if (moveType(moveDetail) === t1 || moveType(moveDetail) === t2) {
            score *= 1.5;
        }
        return score;
    }

    function generateMovesForSlot(slotIndex) {
        var slot = team[slotIndex];
        if (!slot.pokemon) {
            return Promise.resolve();
        }
        return loadPokemonMoves(slot.pokemon.id).then(function (moves) {
            var availableMoves = moves || [];
            var seen = {};
            availableMoves = availableMoves.filter(function (move) {
            if (seen[move.name]) {
                return false;
            }
            seen[move.name] = true;
            return true;
        });

            var scored = availableMoves.map(function (detail) {
                return { detail: detail, score: scoreMove(detail, slot.pokemon) };
            });
            scored.sort(function (a, b) { return b.score - a.score; });

            var selected = [];
            var usedTypes = {};
            scored.forEach(function (entry) {
                if (selected.length >= 4) {
                    return;
                }
                var type = moveType(entry.detail);
                if (type && !usedTypes[type]) {
                    usedTypes[type] = true;
                    selected.push(entry.detail);
                }
            });
            scored.forEach(function (entry) {
                if (selected.length >= 4) {
                    return;
                }
                if (selected.indexOf(entry.detail) === -1) {
                    selected.push(entry.detail);
                }
            });

            slot.moves = [null, null, null, null];
            selected.forEach(function (detail, index) {
                slot.moves[index] = {
                    name: detail.name,
                    type: moveType(detail),
                    powerValue: detail.powerValue || 0,
                    accuracy: detail.accuracy || 100
                };
            });
        });
    }

    function autoTeam() {
        if (busy || !allPokemon.length) {
            return;
        }
        setBusy(true);
        setStatus('BUILDING TEAM...', false);

        var usedIds = {};
        team.forEach(function (slot) {
            if (slot.pokemon) {
                usedIds[slot.pokemon.id] = true;
            }
        });

        team.forEach(function (slot, index) {
            if (slot.pokemon) {
                return;
            }
            var candidates = allPokemon.filter(function (pokemon) {
                return !usedIds[pokemon.id];
            }).map(function (pokemon) {
                return { pokemon: pokemon, score: scoreCandidate(pokemon) };
            }).sort(function (a, b) {
                return b.score - a.score;
            });
            if (!candidates.length) {
                return;
            }
            var pool = candidates.slice(0, Math.min(3, candidates.length));
            var chosen = pool[Math.floor(Math.random() * pool.length)].pokemon;
            team[index] = { pokemon: chosen, moves: [null, null, null, null] };
            usedIds[chosen.id] = true;
        });

        renderSlots();
        renderCoverage();
        setStatus('LOADING MOVES...', false);

        team.reduce(function (chain, slot, index) {
            return chain.then(function () {
                return slot.pokemon ? generateMovesForSlot(index) : Promise.resolve();
            });
        }, Promise.resolve()).then(function () {
            renderSlots();
            renderCoverage();
            setStatus('AUTO TEAM READY', false);
            setBusy(false);
        }).catch(function () {
            setStatus('AUTO TEAM PARTIAL', true);
            setBusy(false);
        });
    }

    function loadRoster() {
        fetch(API + '/api/pokemon/ranked').then(function (response) {
            if (!response.ok) {
                throw new Error('HTTP ' + response.status);
            }
            return response.json();
        }).then(function (data) {
            allPokemon = data;
            document.getElementById('roster-count').textContent = data.length + ' SPECIES READY';
            setStatus('MANUAL PICK OR AUTO GENERATE', false);
            setBusy(false);
        }).catch(function () {
            document.getElementById('roster-count').textContent = 'DATABASE OFFLINE';
            setStatus('START THE APP FIRST', true);
        });
    }

    document.querySelectorAll('.tab-btn').forEach(function (button) {
        button.addEventListener('click', function () {
            activeMode = button.dataset.mode;
            document.querySelectorAll('.tab-btn').forEach(function (item) {
                item.classList.toggle('active', item.dataset.mode === activeMode);
            });
            renderCoverage();
        });
    });

    document.getElementById('auto-team-btn').addEventListener('click', autoTeam);
    document.getElementById('pokemon-search').addEventListener('input', function () {
        renderPokemonPicker(this.value);
    });
    document.getElementById('move-search').addEventListener('input', function () {
        var slot = team[movePickerSlot];
        if (!slot || !slot.pokemon) {
            return;
        }
        loadPokemonMoves(slot.pokemon.id).then(function (moves) {
            renderMovePicker(moves || [], document.getElementById('move-search').value);
        });
    });
    document.getElementById('pokemon-close').addEventListener('click', closePokemonPicker);
    document.getElementById('move-close').addEventListener('click', closeMovePicker);
    document.getElementById('pokemon-overlay').addEventListener('click', function (event) {
        if (event.target === this) {
            closePokemonPicker();
        }
    });
    document.getElementById('move-overlay').addEventListener('click', function (event) {
        if (event.target === this) {
            closeMovePicker();
        }
    });
    document.getElementById('slots').addEventListener('click', function (event) {
        var button = event.target.closest('.moves-auto');
        if (!button || busy) {
            return;
        }
        var slotIndex = Number(button.dataset.slot);
        setBusy(true);
        setStatus('FETCHING MOVES...', false);
        generateMovesForSlot(slotIndex).then(function () {
            renderSlots();
            renderCoverage();
            setStatus('MOVES READY', false);
            setBusy(false);
        }).catch(function () {
            setStatus('MOVE FETCH FAILED', true);
            setBusy(false);
        });
    });

    renderSlots();
    renderCoverage();
    loadRoster();
})();
