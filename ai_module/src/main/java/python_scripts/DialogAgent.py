import openai
from openai import OpenAI
import json
from typing import Dict, Any
import BaseAgent

class DialogAgent(BaseAgent):
    def __init__(self, client: OpenAI):
        super().__init__(client)
        self.system_prompt = """Ты диалоговый агент. Формулируй сообщения:
        - Если нужно уточнить данные: {"type": "question", "text": "..."}
        - Если есть ошибки: {"type": "error", "text": "Конфликты: ..."}
        - Если всё ок: {"type": "success", "text": "Данные приняты!"}"""

    def execute(self, context: Dict[str, Any]) -> Dict[str, Any]:
        response = self.client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": self.system_prompt},
                {"role": "user", "content": json.dumps(context)}
            ]
        )
        return json.loads(response.choices[0].message.content)
