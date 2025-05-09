document.getElementById('chatBotButton').onclick = function () {
    document.getElementById('chatWindow').style.display = 'flex';
    loadChatHistory();
};

document.querySelector('.chat-close-button button').onclick = function () {
    document.getElementById('chatWindow').style.display = 'none';
};

function loadChatHistory() {
    fetch('/api/chat/history')
        .then(response => response.json())
        .then(messages => {
            const chatHistory = document.getElementById('chatHistory');
            chatHistory.innerHTML = '';
            messages.forEach(msg => appendMessage(msg.sender, msg.message));
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
    const fileInput = document.getElementById('fileInput');
    const message = input.value.trim();
    const file = fileInput.files[0];

    if (!message && !file) return;

    if (message) {
        appendMessage('You', message);
    }

    const formData = new FormData();
    if (message) formData.append('message', message);
    if (file) formData.append('file', file);

    fetch('/api/chat', {
        method: 'POST',
        body: formData
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

    input.value = '';
    fileInput.value = '';
};
