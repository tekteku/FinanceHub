import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './components/auth/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ProjectListComponent } from './components/projects/project-list/project-list.component';
import { CreateProjectComponent } from './components/projects/create-project/create-project.component';
import { ProjectDetailComponent } from './components/projects/project-detail/project-detail.component';
import { MyInvestmentsComponent } from './components/projects/my-investments/my-investments.component';
import { NotificationsListComponent } from './components/notifications/notifications-list/notifications-list.component';
import { InvestorProfileComponent } from './components/investor/investor-profile/investor-profile.component';
import { authGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'projects', component: ProjectListComponent, canActivate: [authGuard] },
  { path: 'projects/create', component: CreateProjectComponent, canActivate: [authGuard] },
  { path: 'projects/:id', component: ProjectDetailComponent, canActivate: [authGuard] },
  { path: 'my-investments', component: MyInvestmentsComponent, canActivate: [authGuard] },
  { path: 'notifications', component: NotificationsListComponent, canActivate: [authGuard] },
  { path: 'profile', component: InvestorProfileComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }