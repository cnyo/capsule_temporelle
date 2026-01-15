import {Routes} from '@angular/router';
import {CapsuleDetail} from './components/capsule-detail/capsule-detail';
import {CapsuleHome} from './components/capsule-home/capsule-home';

export const routes: Routes = [
  {
    path: '', component: CapsuleHome
  },
  {
    path: 'capsule/:id', component: CapsuleDetail,
  }
];
