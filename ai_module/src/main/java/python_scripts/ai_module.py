import openai
from openai import OpenAI
import json
import logging
from typing import Dict, Any
import BaseAgent
import ParserAgent
import ValidatorAgent
import DialogAgent

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class Orchestrator:
    def __init__(self):
        self.client = OpenAI(
            api_key="sk-ZEJF4veMeyD1xXW7iXGerDu4HFVmLIfo",
            base_url="https://api.proxyapi.ru/openai/v1",
        )
        self.agents = {
            "parser": ParserAgent(self.client),
            "validator": ValidatorAgent(self.client),
            "dialog": DialogAgent(self.client)
        }

    def process_request(self, user_input: str) -> Dict[str, Any]:
        context = {"user_input": user_input}

        # Парсинг
        context["parsed_data"] = self.agents["parser"].execute(context)

        # Если нужно больше данных
        if context["parsed_data"].get("status") == "need_more_data":
            return self.agents["dialog"].execute(context["parsed_data"])

        # Валидация
        context["validation"] = self.agents["validator"].execute(context)

        # Генерация ответа
        return self.agents["dialog"].execute(context)

if __name__ == "__main__":
    orchestrator = Orchestrator()
    user_input = "Добавь лекцию по физике для группы Ф-202 в среду утром"
    result = orchestrator.process_request(user_input)
    print("Результат:", json.dumps(result, indent=2, ensure_ascii=False))