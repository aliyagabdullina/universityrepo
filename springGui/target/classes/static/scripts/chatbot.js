document.getElementById('chatBotButton').onclick = function () {
    document.getElementById('chatWindow').style.display = 'flex';
    loadChatHistory();
};

document.getElementById('closeChat').onclick = function () {
    document.getElementById('chatWindow').style.display = 'none';
};

function loadChatHistory() {
    fetch('/api/chat/history') // Получаем историю чатов с сервера
        .then(response => response.json())
        .then(messages => {
            const chatHistory = document.getElementById('chatHistory');
            chatHistory.innerHTML = ''; // Очищаем старую историю

            // Перебираем каждое сообщение в истории чата
            messages.forEach(msg => {
                // Добавляем сообщение от пользователя
                if (msg.message) {
                    appendMessage('You', msg.message);
                }
                // Добавляем сообщение от бота
                if (msg.response) {
                    appendMessage('Chatbot', msg.response);
                }
            });
        })
        .catch(error => {
            console.error('Ошибка при загрузке истории чата:', error);
        });
}

function appendMessage(sender, message) {
    const chatHistory = document.getElementById('chatHistory');
    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message', sender === 'You' ? 'user' : 'bot');
    messageDiv.innerHTML = `<strong>${sender}:</strong> ${message}`;
    chatHistory.appendChild(messageDiv);
    chatHistory.scrollTop = chatHistory.scrollHeight;
}

document.getElementById('sendMessage').onclick = function () {
    const input = document.getElementById('chatInput');
    const message = input.value.trim();
    if (!message) return;

    appendMessage('You', message);
    input.value = '';

    fetch('/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message })
    })
    .then(response => response.json())
    .then(data => {
        if (data.reply) {
            appendMessage('Chatbot', data.reply);
        } else {
            appendMessage('Chatbot', '(нет ответа от сервера)');
        }
    })
    .catch(() => {
        appendMessage('Chatbot', 'Ошибка при соединении с сервером');
    });
};
