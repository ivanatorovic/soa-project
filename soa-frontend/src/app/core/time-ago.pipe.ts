import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timeAgo',
  standalone: true,
  pure: false,
})
export class TimeAgoPipe implements PipeTransform {
  transform(value: string | Date | null | undefined): string {
    if (!value) {
      return '';
    }

    const date = value instanceof Date ? value : new Date(value);
    const now = new Date();

    const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

    if (isNaN(diffInSeconds)) {
      return '';
    }

    if (diffInSeconds < 10) {
      return 'upravo sada';
    }

    if (diffInSeconds < 60) {
      return `pre ${diffInSeconds} s`;
    }

    const diffInMinutes = Math.floor(diffInSeconds / 60);
    if (diffInMinutes < 60) {
      return diffInMinutes === 1 ? 'pre 1 min' : `pre ${diffInMinutes} min`;
    }

    const diffInHours = Math.floor(diffInMinutes / 60);
    if (diffInHours < 24) {
      return diffInHours === 1 ? 'pre 1 h' : `pre ${diffInHours} h`;
    }

    const diffInDays = Math.floor(diffInHours / 24);
    if (diffInDays < 7) {
      return diffInDays === 1 ? 'pre 1 dan' : `pre ${diffInDays} dana`;
    }

    const diffInWeeks = Math.floor(diffInDays / 7);
    if (diffInWeeks < 5) {
      return diffInWeeks === 1 ? 'pre 1 nedelju' : `pre ${diffInWeeks} nedelje`;
    }

    const diffInMonths = Math.floor(diffInDays / 30);
    if (diffInMonths < 12) {
      return diffInMonths === 1 ? 'pre 1 mesec' : `pre ${diffInMonths} meseca`;
    }

    const diffInYears = Math.floor(diffInDays / 365);
    return diffInYears === 1 ? 'pre 1 godinu' : `pre ${diffInYears} godina`;
  }
}
