let estruturaSelecionada = 0;

function selecionarEstrutura(id, btn) {
    estruturaSelecionada = id;
    document.querySelectorAll('.btn-build').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
}

// Inicia novo jogo a partir do modal
async function iniciarJogo(event) {
    event.preventDefault();
    const civ = document.getElementById('input-civ').value.trim();
    const leader = document.getElementById('input-leader').value.trim();

    if (!civ || !leader) return;

    try {
        const res = await fetch(`/api/iniciar?civ=${encodeURIComponent(civ)}&leader=${encodeURIComponent(leader)}`);
        const dados = await res.json();

        if (dados.sucesso) {
            document.getElementById('login-modal').classList.add('hidden');
            document.getElementById('app-container').classList.remove('hidden');
            carregarEstado();
        }
    } catch (err) {
        console.error('Erro ao iniciar jogo:', err);
    }
}

// Carrega mapa, estatísticas e recursos
async function carregarEstado() {
    try {
        const res = await fetch('/api/estado');
        const dados = await res.json();

        if (!dados.gameStarted) {
            document.getElementById('login-modal').classList.remove('hidden');
            document.getElementById('app-container').classList.add('hidden');
            return;
        }

        // Atualiza UI de Estatísticas
        document.getElementById('display-civ').innerText = dados.civName;
        document.getElementById('display-leader').innerText = dados.leaderName;
        document.getElementById('display-turn').innerText = dados.turn;

        document.getElementById('res-pop').innerText = dados.populacao;
        document.getElementById('res-madeira').innerText = dados.recursos.madeira;
        document.getElementById('res-comida').innerText = dados.recursos.comida;
        document.getElementById('res-pedra').innerText = dados.recursos.pedra;

        if (dados.mensagem) {
            document.getElementById('log-text').innerText = dados.mensagem;
        }

        // Renderiza o Mapa
        const tabuleiro = document.getElementById('tabuleiro');
        tabuleiro.innerHTML = '';

        const iconeTerreno = {
            'agua': '',
            'grama': '',
            'floresta': '🌲',
            'montanha': '⛰️',
            'cidade': '🏰',
            'casa': '🏠',
            'fazenda': '🌾',
            'acampamento': '🪵',
            'pedreira': '⛏️'
        };

        dados.mapa.forEach((linha, y) => {
            linha.forEach((terreno, x) => {
                const tile = document.createElement('div');
                tile.className = `tile ${terreno}`;
                tile.innerText = iconeTerreno[terreno] || '';
                tile.title = `(${x}, ${y}) - ${terreno.toUpperCase()}`;
                
                tile.onclick = () => construirNoTile(x, y);

                tabuleiro.appendChild(tile);
            });
        });

    } catch (err) {
        console.error('Erro ao atualizar estado:', err);
    }
}

// Envia comando de construção
async function construirNoTile(x, y) {
    try {
        const res = await fetch(`/api/construir?x=${x}&y=${y}&id=${estruturaSelecionada}`);
        const resultado = await res.json();
        carregarEstado();
    } catch (err) {
        console.error('Erro ao construir:', err);
    }
}

// Passar Turno
async function passarTurno() {
    try {
        await fetch('/api/turno');
        carregarEstado();
    } catch (err) {
        console.error('Erro ao passar turno:', err);
    }
}

// Verifica estado ao carregar a página
carregarEstado();