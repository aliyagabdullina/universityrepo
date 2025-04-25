// Open chatbot window
document.getElementById('chatBotButton').onclick = function () {
  document.getElementById('chatbotWindow').style.display = 'block';
  loadChatHistory();
};

// Close chatbot window
document.getElementById('closeChat').onclick = function () {
  document.getElementById('chatbotWindow').style.display = 'none';
};

// Send message to the server
document.getElementById('sendMessage').onclick = function () {
  const message = document.getElementById('chatInput').value;
  if (message.trim() === '') return;

  // Send message to backend
  fetch('/api/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ message })
  }).then(response => response.json())
    .then(data => {
      // Append message to chat history
      appendMessage('You', message);
      appendMessage('AI', data.reply);
      document.getElementById('chatInput').value = '';
    });
};

// Load chat history when the window is opened
function loadChatHistory() {
  fetch('/api/chat/history')
    .then(response => response.json())
    .then(messages => {
      const chatHistory = document.getElementById('chatHistory');
      chatHistory.innerHTML = ''; // Clear previous history
      messages.forEach(msg => appendMessage(msg.sender, msg.message));
    });
}

// Append message to chat history
function appendMessage(sender, message) {
  const messageDiv = document.createElement('div');
  messageDiv.classList.add('message');
  messageDiv.innerHTML = `<strong>${sender}:</strong> ${message}`;
  document.getElementById('chatHistory').appendChild(messageDiv);
}
