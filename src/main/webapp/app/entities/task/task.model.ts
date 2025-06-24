import dayjs from 'dayjs/esm';

export interface ITask {
  id: number;
  description?: string | null;
  completed?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  targetDate?: dayjs.Dayjs | null;
}

export type NewTask = Omit<ITask, 'id'> & { id: null };
