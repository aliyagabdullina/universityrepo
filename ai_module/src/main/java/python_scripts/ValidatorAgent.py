import openai
from openai import OpenAI
import json
from typing import Dict, Any
import BaseAgent

class ValidatorAgent(BaseAgent):
    def __init__(self, client: OpenAI):
        super().__init__(client)
        self.system_prompt = """Ты валидатор расписаний. Ищи конфликты:
        1. Одна аудитория в одно время
        2. Преподаватель в двух местах
        3. Группа имеет > max_lectures_per_day
        Верни {"conflicts": ["описание конфликта"], "status": "ok/error"}."""

    def execute(self, context: Dict[str, Any]) -> Dict[str, Any]:
        response = self.client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": self.system_prompt},
                {"role": "user", "content": json.dumps(context["parsed_data"])}
            ]
        )
        return json.loads(response.choices[0].message.content)
