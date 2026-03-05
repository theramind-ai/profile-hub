import { Component } from '@angular/core';

@Component({
    selector: 'app-gallery',
    standalone: true,
    template: `<h1>Public Gallery</h1><p>Public documents will appear here...</p>`,
    styles: [`:host { display: block; padding: 30px; }`]
})
export class PublicGalleryComponent { }
