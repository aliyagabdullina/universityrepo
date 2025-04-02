from openai import OpenAI
from typing import Dict, Any


class BaseAgent:
    def __init__(self, client: OpenAI):
        self.client = client
        self.system_prompt = ""

    def execute(self, context: Dict[str, Any]) -> Dict[str, Any]:
        raise NotImplementedError