import openai
from openai import OpenAI
import json
import logging
from typing import Dict, Any
from BaseAgent import BaseAgent


class ParserAgent(BaseAgent):
    def __init__(self, client: OpenAI):
        super().__init__(client)
        self.system_prompt = """Ты парсер данных. Извлекай данные в формате JSON для передачи их на сервер.
        Если данных мало, верни {"status": "need_more_data", "question": "Текст вопроса"}."""

    def execute(self, context: Dict[str, Any]) -> Dict[str, Any]:
        response = self.client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": self.system_prompt},
                {"role": "user", "content": context["user_input"]}
            ],
            temperature=0.3
        )
        return json.loads(response.choices[0].message.content)
