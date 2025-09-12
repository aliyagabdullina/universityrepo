from flask import Flask, request, jsonify
from datetime import datetime
from ai import Orchestrator  # <- Импорт из твоего модуля

app = Flask(__name__)
orchestrator = Orchestrator()

@app.route('/llm', methods=['POST'])
def llm_response():
    data = request.get_json()
    user_message = data.get('message', '')

    try:
        result = orchestrator.process_request(user_message)
        reply = result.get('reply') or result.get('message') or str(result)
    except Exception as e:
        reply = f"Ошибка при обработке запроса: {str(e)}"

    return jsonify({
        'reply': reply,
        'timestamp': datetime.now().isoformat()
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)
