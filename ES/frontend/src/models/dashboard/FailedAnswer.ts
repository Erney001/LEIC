import { ISOtoString } from '@/services/ConvertDateService';
import Question from '@/models/management/Question';

export default class FailedAnswer {
  id!: number;
  questionContent!: string;
  answered!: boolean;
  collected!: string;
  questionId!: number;

  constructor(jsonObj?: FailedAnswer) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.questionContent = jsonObj.questionContent;
      this.answered = jsonObj.answered;
      this.questionId = jsonObj.questionId;
      if (jsonObj.collected) this.collected = ISOtoString(jsonObj.collected);
    }
  }
}
