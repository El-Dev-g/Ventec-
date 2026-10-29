import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Zooz Marketplace",
  description: "The official Next.js multi-vendor marketplace for Zooz",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className="antialiased min-h-screen flex flex-col">
        {children}
      </body>
    </html>
  );
}
