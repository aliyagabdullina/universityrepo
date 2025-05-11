// Открытие/закрытие окна чата
document.addEventListener('DOMContentLoaded', function () {
    const chatBotButton = document.getElementById('chatBotButton');
    const chatWindow = document.getElementById('chatWindow');
    const closeBtn = document.getElementById('closeChat');

    chatBotButton.onclick = () => {
        chatWindow.style.display = 'flex';
        loadChatHistory();
    };

    closeBtn.onclick = () => {
        chatWindow.style.display = 'none';
    };
});

// Загрузка истории

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

// Добавление сообщений в чат
function appendMessage(sender, message) {
    const chatHistory = document.getElementById('chatHistory');
    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message', sender === 'You' ? 'user' : 'bot');
    messageDiv.innerHTML = `<strong>${sender}:</strong> ${message}`;
    chatHistory.appendChild(messageDiv);
    chatHistory.scrollTop = chatHistory.scrollHeight;
}

// Отправка сообщения и/или файла
document.addEventListener('DOMContentLoaded', function () {
    const sendButton = document.getElementById('sendMessage');

    sendButton.onclick = function () {
        const input = document.getElementById('chatInput');
        const fileInput = document.getElementById('fileInput');
        const message = input.value.trim();
        const file = fileInput.files[0];

        if (!message && !file) return;

        if (message) appendMessage('You', message);

        const formData = new FormData();
        if (message) formData.append('message', message);
        if (file) formData.append('file', file);

        fetch('/api/chat/upload', {
            method: 'POST',
            body: formData
        })
        .then(res => res.json())
        .then(data => {
            appendMessage('Chatbot', data.reply || '(нет ответа от сервера)');
        })
        .catch(() => {
            appendMessage('Chatbot', 'Ошибка при соединении с сервером');
        });

        input.value = '';
        fileInput.value = '';
    };
});
