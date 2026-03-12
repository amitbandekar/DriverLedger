# IT Oriental Institute

Official website for **IT Oriental Institute** — a learning center offering computer courses, packages, and enrollment support.

Built with React, TypeScript, and Vite. Includes a contact form with WhatsApp notification support and an admin panel for lead management and visit tracking.

---

## Tech Stack

- React 18 + TypeScript
- Vite
- Tailwind CSS + shadcn/ui
- React Router, React Query
- WhatsApp Web.js (for auto-messaging leads)
- Flask API (backend, hosted on PythonAnywhere)

---

## Getting Started

### Prerequisites

- Node.js (v18+)
- npm or bun

### Install dependencies

```bash
npm install
```

### Run locally

```bash
npm run dev
```

### Build for production

```bash
npx vite build
```

---

## WhatsApp Sender

The `whatsapp-sender/` folder contains a Node.js script that reads pending inquiries from the Flask API and sends them to the admin via WhatsApp.

### Setup

1. Go into the folder:

```bash
cd whatsapp-sender
npm install
```

2. Edit `config.json` with your Flask API URL and admin WhatsApp number:

```json
{
  "flask_api": "https://your-api-url.com",
  "admin_number": "91XXXXXXXXXX"
}
```

3. Run the sender:

```bash
node server.js
```

Scan the QR code on first run to authenticate WhatsApp. It will then fetch all queued messages and send them automatically.

---

## Project Structure

```
src/
├── components/     # UI components (Header, Hero, Courses, etc.)
├── pages/          # Route pages (Home, Login, Leads, TrackVisits)
├── hooks/          # Custom React hooks
└── lib/            # Utility functions

whatsapp-sender/
├── server.js       # WhatsApp messaging script
└── config.json     # API and phone number config
```

---

## License

ISC
