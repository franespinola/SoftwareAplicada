import dayjs from 'dayjs/esm';

import { ITask, NewTask } from './task.model';

export const sampleWithRequiredData: ITask = {
  id: 9181,
  description: 'or consequently',
  completed: false,
  createdAt: dayjs('2025-06-24T22:38'),
};

export const sampleWithPartialData: ITask = {
  id: 20483,
  description: 'positively really whoa',
  completed: true,
  createdAt: dayjs('2025-06-24T20:51'),
  targetDate: dayjs('2025-06-24T03:34'),
};

export const sampleWithFullData: ITask = {
  id: 13396,
  description: 'where wherever',
  completed: true,
  createdAt: dayjs('2025-06-24T15:47'),
  targetDate: dayjs('2025-06-24T13:01'),
};

export const sampleWithNewData: NewTask = {
  description: 'expostulate',
  completed: true,
  createdAt: dayjs('2025-06-24T12:42'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
