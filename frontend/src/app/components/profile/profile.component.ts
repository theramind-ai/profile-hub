import { Component } from '@angular/core';

@Component({
    selector: 'app-profile',
    standalone: true,
    template: `<h1>My Profile</h1><p>Profile editing coming soon...</p>`,
    styles: [`:host { display: block; padding: 30px; }`]
})
export class ProfileComponent { }
