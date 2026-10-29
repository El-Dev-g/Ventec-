import React from "react";

// Mock products data to represent a functional mockup
const CATEGORIES = ["All", "Digital Assets", "E-Books", "Social Accents", "Design Templates", "Source Code"];
const FEATURED_PRODUCTS = [
  {
    id: 1,
    title: "Zooz Premium Chat Theme Pack",
    category: "Digital Assets",
    price: 12.99,
    rating: 4.9,
    seller: "@alex_zooz",
    color: "border-t-[#4285F4]"
  },
  {
    id: 2,
    title: "Social Growth Strategy Playbook",
    category: "E-Books",
    price: 19.99,
    rating: 4.8,
    seller: "@author_pro",
    color: "border-t-[#EA4335]"
  },
  {
    id: 3,
    title: "Animated SVG Stickers Bundle",
    category: "Design Templates",
    price: 8.50,
    rating: 4.7,
    seller: "@sticker_creative",
    color: "border-t-[#FBBC05]"
  },
  {
    id: 4,
    title: "Next.js + Room DB Kotlin Connector",
    category: "Source Code",
    price: 49.00,
    rating: 5.0,
    seller: "@dev_genius",
    color: "border-t-[#34A853]"
  }
];

export default function Home() {
  return (
    <div className="flex-grow flex flex-col bg-slate-50 dark:bg-slate-900 text-slate-900 dark:text-slate-100 transition-colors">
      {/* Header */}
      <header className="sticky top-0 z-50 bg-white/80 dark:bg-slate-900/80 backdrop-blur-md border-b border-slate-200 dark:border-slate-800">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <span className="text-2xl font-black tracking-tighter">
              <span className="text-[#4285F4]">z</span>
              <span className="text-[#EA4335]">o</span>
              <span className="text-[#FBBC05]">o</span>
              <span className="text-[#34A853]">z</span>
              <span className="text-slate-700 dark:text-slate-300 font-medium text-lg ml-0.5">marketplace</span>
            </span>
          </div>

          <div className="hidden md:flex space-x-6">
            <a href="#" className="hover:text-[#4285F4] transition-colors font-medium">Explore</a>
            <a href="#" className="hover:text-[#EA4335] transition-colors font-medium">For Sellers</a>
            <a href="#" className="hover:text-[#FBBC05] transition-colors font-medium">Dashboard</a>
            <a href="#" className="hover:text-[#34A853] transition-colors font-medium">Support</a>
          </div>

          <div className="flex items-center space-x-3">
            <button className="px-4 py-2 text-sm font-semibold hover:bg-slate-100 dark:hover:bg-slate-800 rounded-full transition-all">
              Sign In
            </button>
            <button className="px-4 py-2 text-sm font-semibold bg-[#4285F4] hover:bg-[#4285F4]/95 text-white rounded-full transition-all shadow-md active:scale-95">
              Start Selling
            </button>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 flex-grow">
        <div className="text-center max-w-3xl mx-auto mb-16">
          <h1 className="text-4xl sm:text-6xl font-black tracking-tight mb-6">
            Buy and Sell Assets with the{" "}
            <span className="inline-block">
              <span className="text-[#4285F4]">Z</span>
              <span className="text-[#EA4335]">o</span>
              <span className="text-[#FBBC05]">o</span>
              <span className="text-[#34A853]">z</span>
            </span>{" "}
            Community
          </h1>
          <p className="text-lg text-slate-600 dark:text-slate-300 mb-8 leading-relaxed">
            The multi-vendor hub for tailored digital products, e-books, exclusive social layouts, and interactive resources. Configured securely and fully integrated.
          </p>
          <div className="flex flex-col sm:flex-row justify-center gap-4 max-w-md mx-auto">
            <input 
              type="text" 
              placeholder="Search assets, tools, templates..." 
              className="flex-grow px-5 py-3 rounded-full border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-sm focus:outline-none focus:ring-2 focus:ring-[#4285F4] transition-all"
            />
            <button className="px-6 py-3 bg-slate-900 dark:bg-slate-100 text-white dark:text-slate-900 rounded-full text-sm font-semibold hover:opacity-95 transition-all shadow-lg active:scale-95">
              Search
            </button>
          </div>
        </div>

        {/* Categories Bar */}
        <div className="mb-12 border-b border-slate-200 dark:border-slate-800 pb-5">
          <div className="flex space-x-2 overflow-x-auto pb-2 scrollbar-none">
            {CATEGORIES.map((cat, idx) => (
              <button 
                key={idx}
                className={`px-4 py-1.5 rounded-full text-sm font-medium transition-all shrink-0 ${
                  idx === 0 
                  ? "bg-[#4285F4] text-white shadow-sm" 
                  : "bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 hover:border-slate-400 dark:hover:border-slate-500"
                }`}
              >
                {cat}
              </button>
            ))}
          </div>
        </div>

        {/* Product List */}
        <div>
          <div className="flex items-center justify-between mb-8">
            <h2 className="text-2xl font-bold">Featured Listings</h2>
            <a href="#" className="text-sm font-semibold text-[#4285F4] hover:underline">View All &rarr;</a>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {FEATURED_PRODUCTS.map((prod) => (
              <div 
                key={prod.id} 
                className={`bg-white dark:bg-slate-800 rounded-2xl border-t-4 ${prod.color} border border-slate-100 dark:border-slate-700/50 p-6 flex flex-col justify-between shadow-sm hover:shadow-lg transition-all hover:-translate-y-1 duration-200`}
              >
                <div>
                  <div className="text-xs font-semibold text-slate-400 uppercase tracking-widest mb-1">{prod.category}</div>
                  <h3 className="font-bold text-lg mb-2 line-clamp-2">{prod.title}</h3>
                  <div className="text-xs text-slate-500 dark:text-slate-400 mb-4">Seller: <span className="font-medium text-slate-700 dark:text-slate-200">{prod.seller}</span></div>
                </div>

                <div className="flex items-center justify-between mt-4 pt-4 border-t border-slate-100 dark:border-slate-700/50">
                  <span className="font-extrabold text-xl">${prod.price.toFixed(2)}</span>
                  <button className="px-3 py-1.5 bg-slate-900 dark:bg-slate-100 hover:bg-[#34A853] dark:hover:bg-[#34A853] hover:text-white dark:hover:text-white text-white dark:text-slate-900 text-xs font-bold rounded-full transition-all">
                    Detail View
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Showcase / CTA Block */}
        <div className="mt-20 bg-gradient-to-br from-slate-900 to-indigo-950 text-white rounded-3xl p-8 sm:p-12 relative overflow-hidden shadow-xl">
          <div className="relative z-10 max-w-2xl">
            <h2 className="text-3xl sm:text-4xl font-black mb-4">Host Your Own Vendor Booth</h2>
            <p className="text-slate-300 mb-6 leading-relaxed">
              Unlock direct monetization, built-in analytics, secure digital escrow, and instant integration with Zooz social channels. Start with 0% platform fees for your first month.
            </p>
            <div className="flex flex-wrap gap-4">
              <button className="px-6 py-3 bg-white text-slate-950 font-bold rounded-full hover:bg-slate-100 transition-all select-none active:scale-95 text-sm">
                Register Storefront
              </button>
              <button className="px-6 py-3 bg-white/10 text-white border border-white/20 font-bold rounded-full hover:bg-white/20 transition-all select-none active:scale-95 text-sm">
                Read Documentation
              </button>
            </div>
          </div>
          {/* Decorative multi-color accent glows */}
          <div className="absolute top-0 right-0 w-64 h-64 bg-[#4285F4]/20 blur-3xl rounded-full translate-x-12 -translate-y-12"></div>
          <div className="absolute bottom-0 right-1/4 w-48 h-48 bg-[#34A853]/10 blur-3xl rounded-full translate-y-12"></div>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-white dark:bg-slate-950 border-t border-slate-200 dark:border-slate-800 py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col sm:flex-row justify-between items-center gap-4 text-xs text-slate-500">
          <div>
            &copy; 2026 <span className="font-bold text-slate-700 dark:text-slate-300">zooz</span>official. All rights reserved.
          </div>
          <div className="flex space-x-6">
            <a href="#" className="hover:underline">Privacy Policy</a>
            <a href="#" className="hover:underline">Terms of Service</a>
            <a href="#" className="hover:underline">Market Guidelines</a>
          </div>
        </div>
      </footer>
    </div>
  );
}
