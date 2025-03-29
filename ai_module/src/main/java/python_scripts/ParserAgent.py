import openai
from openai import OpenAI
import json
import logging
from typing import Dict, Any
import BaseAgent

class ParserAgent(BaseAgent):
    def __init__(self, client: OpenAI):
        super().__init__(client)
        self.system_prompt = """Ты парсер расписаний. Извлекай данные в формате JSON:
        {
            "courses": [{
                "name": "Название",
                "professor": "Имя",
                "groups": ["Группа"],
                "duration": 2,
                "preferred_time": "понедельник 9:00"
            }],
            "constraints": {
                "work_hours": "9:00-18:00",
                "max_lectures_per_day": 4
            }
        }
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
