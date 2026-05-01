package com.example.productcrud.config;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.repository.CategoryRepository;
import com.example.productcrud.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public DataInitializer(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void init() {
        if (categoryRepository.count() == 0) {
            Category elektronik = new Category("Elektronik", "Produk elektronik terkini");
            Category buku = new Category("Buku", "Buku bacaan dan referensi");
            Category makanan = new Category("Makanan", "Makanan dan minuman");
            Category pakaian = new Category("Pakaian", "Pakaian dan aksesoris");
            Category olahraga = new Category("Olahraga", "Perlengkapan olahraga dan kebugaran");

            categoryRepository.saveAll(Arrays.asList(elektronik, buku, makanan, pakaian, olahraga));

            // Generate 150 realistic products
            List<Product> products = Arrays.asList(
                new Product("Laptop ASUS ROG Strix G15", 18500000, 8, "Laptop gaming dengan AMD Ryzen 7 6800H, RTX 4060, 16GB RAM, 512GB SSD"),
                new Product("MacBook Air M2 13-inch", 15999000, 15, "MacBook ultraportable dengan chip Apple M2, 8GB RAM, 256GB SSD"),
                new Product("iPhone 15 Pro Max 256GB", 18500000, 25, "iPhone flagship dengan chip A17 Pro, sistem kamera 48MP, titanium design"),
                new Product("Samsung Galaxy S24 Ultra", 18500000, 20, "Smartphone flagship Samsung dengan S Pen, kamera 200MP, AI features"),
                new Product("Sony WH-1000XM5", 4500000, 35, "Headphone wireless dengan noise cancelling terbaik di kelasnya"),
                new Product("AirPods Pro 2nd Gen", 2999000, 50, "Earbuds wireless dengan ANC aktif dan spatial audio"),
                new Product("Apple Watch Series 9", 7999000, 18, "Smartwatch dengan fitur kesehatan, fitness tracking, dan always-on Retina"),
                new Product("iPad Pro 12.9 M2", 23000000, 12, "iPad profesional dengan Mini-LED display, Apple M2 chip, 128GB"),
                new Product("Dell XPS 13 Plus", 22000000, 8, "Laptop ultrabook premium dengan Intel Core i7, 32GB RAM, 1TB SSD"),
                new Product("Lenovo ThinkPad X1 Carbon", 24500000, 10, "Laptop business class dengan Intel Core i7, 16GB RAM, 512GB SSD"),
                new Product("ASUS ZenBook 14 OLED", 13500000, 14, "Laptop tipis dengan OLED display, AMD Ryzen 7, 16GB RAM"),
                new Product("Samsung Galaxy Tab S9 Ultra", 15500000, 9, "Tablet flagship dengan display 14.6 AMOLED, S Pen included"),
                new Product("Canon EOS R5 Mirrorless", 42000000, 6, "Kamera mirrorless full-frame dengan 45MP, 8K video, in-body stabilization"),
                new Product("Sony A7 IV Mirrorless", 28500000, 7, "Kamera mirrorless full-frame dengan 33MP, 4K 60fps, autofokus canggih"),
                new Product("GoPro Hero 12 Black", 6500000, 22, "Kamera action dengan 5.3K video, HyperSmooth 6.0, waterproof"),
                new Product("Nintendo Switch OLED", 5999000, 30, "Console gaming dengan layar OLED 7 inch, 64GB storage"),
                new Product("PlayStation 5", 9500000, 12, "Console next-gen dengan SSD ultra-fast, 4K 120Hz, ray tracing"),
                new Product("Xbox Series X", 9200000, 10, "Console next-gen dengan 12TF teraflops, 4K gaming, Quick Resume"),
                new Product("Razer Blade 15 Advanced", 28500000, 5, "Laptop gaming dengan RTX 4080, 32GB RAM, 1TB SSD, QHD 240Hz"),
                new Product("Logitech MX Master 3S", 1750000, 60, "Mouse wireless ergonomis dengan 8K DPI, quiet clicks, USB-C charging"),
                new Product("Keychron K2 Mechanical", 1250000, 45, "Keyboard mechanical wireless dengan backlit RGB, hot-swappable switches"),
                new Product("Elgato Stream Deck XL", 4750000, 8, "Controller streaming dengan 32 LCD keys untuk OBS, Twitch, YouTube"),
                new Product("Blue Yeti Nano Microphone", 2100000, 28, "USB microphone dengan tri-capsule array, Bluebroadcaster voice technology"),
                new Product("Samsung 49\" Odyssey G9", 18500000, 4, "Monitor gaming curved ultra-wide 49\" 240Hz, QLED, 1ms response"),
                new Product("LG C2 65\" OLED", 18500000, 7, "Smart TV OLED 4K dengan Dolby Vision, webOS, gaming optimized"),
                new Product("DJI Mini 3 Pro", 12000000, 11, "Drone portable dengan kamera 48MP, 4K 60fps, obstacle sensing"),
                new Product("Kindle Paperwhite 11th Gen", 2499000, 40, "E-reader dengan 6.8 display, 300ppi, waterproof, 8GB storage"),
                new Product("Bose QuietComfort 45", 3500000, 25, "Headphone over-ear dengan premium noise cancellation, 24hr battery"),
                new Product("JBL Flip 6 Portable", 1999000, 55, "Speaker Bluetooth portable waterproof, 12hr playtime, PartyBoost"),
                new Product("Apple Studio Display", 26000000, 3, "Monitor 5K Retina dengan nano-texture, 12MP Center Stage camera"),
                new Product("MSI GE77 Raider", 32000000, 4, "Laptop gaming flagship dengan RTX 4090, i9-12900HX, 64GB RAM"),
                new Product("ASUS ROG Swift PG32UQX", 28000000, 2, "Monitor gaming 32\" 4K 144Hz, mini-LED, HDR1400, G-Sync"),
                new Product("Razer DeathAdder V3 Pro", 1999000, 38, "Gaming mouse wireless dengan 30K DPI, 90hr battery, lightweight 63g"),
                new Product("SteelSeries Arctis Nova Pro", 6500000, 15, "Gaming headset wireless dengan hi-fi audio, hot-swappable battery"),
                new Product("Corsair K100 RGB", 3500000, 20, "Gaming keyboard mechanical optical dengan RGB, aircraft-grade aluminum"),
                new Product("WD Black SN850X 2TB", 3500000, 42, "NVMe SSD PCIe 4.0 untuk gaming, 7300MB/s, heatsink included"),
                new Product("Samsung 980 Pro 2TB", 3200000, 35, "NVMe SSD PCIe 4.0 dengan read 7000MB/s, heatsink version"),
                new Product("Elgato Facecam Pro", 6000000, 9, "Webcam 4K60 dengan sensor Sony, studio-quality optics, AI autofocus"),
                new Product("HyperX Cloud III Wireless", 2500000, 30, "Gaming headset wireless dengan DTS Headphone:X, 120hr battery"),
                new Product("Acer Predator XB273U", 12000000, 6, "Monitor gaming 27\" QHD 240Hz, IPS, G-Sync Compatible"),
                new Product("HP Spectre x360 14", 19500000, 8, "2-in-1 convertible laptop dengan OLED, i7, 16GB, 1TB"),
                
                // Books - 30 products
                new Product("Laskar Pelangi - Andrea Hirata", 95000, 120, "Novel inspiratif tentang perjuangan anak-anak Belitung menggap ilmu"),
                new Product("Sang Pemimpi - Andrea Hirata", 89000, 100, "Sequel Laskar Pelangi tentang mimpi dan cinta"),
                new Product("Negeri 5 Menara - Ahmad Fuadi", 115000, 90, "Novel tentang perjalanan hidup mahasiswa pesantren"),
                new Product("Bumi Manusia - Pramoedya Ananta Toer", 125000, 85, "Klasik sastra Indonesia tentang kolonialisme dan cinta"),
                new Product("Anak Semua Bangsa - Pramoedya", 125000, 80, "Kedua dari Tetralogi Buru Quartet"),
                new Product("Jejak Langkah - Pramoedya", 135000, 75, "Ketiga dari Tetralogi Buru Quartet"),
                new Product("Home - Pramoedya", 135000, 70, "Terakhir dari Tetralogi Buru Quartet"),
                new Product("Pulang - Tere Liye", 150000, 95, "Novel tentang perjuangan keluarga dan perjalanan pulang"),
                new Product("Bukan Cinderella - Ramadhania F.", 65000, 150, "Novel young adult tentang self-love dan empowerment"),
                new Product("The Alchemist - Paulo Coelho", 120000, 110, "Klasik motivasional tentang mengikuti impian dan pertanda"),
                new Product("Atomic Habits - James Clear", 180000, 200, "Buku self-help tentang pembiasaan kecil yang memberikan hasil besar"),
                new Product("Rich Dad Poor Dad - Robert Kiyosaki", 150000, 140, "Buku finansial tentang financial literacy dan investasi"),
                new Product("The Subtle Art of Not Giving a F*ck", 145000, 130, "Buku self-help tentangprioritaskan nilai-nilai yang benar"),
                new Product("Sapiens - Yuval Noah Harari", 220000, 90, "Buku sejarah manusia dari perspektif evolusi dan budaya"),
                new Product("Homo Deus - Yuval Noah Harari", 245000, 75, "Buku tentang masa depan manusia dan AI"),
                new Product("21 Lessons for the 21st Century - Harari", 199000, 85, "Pelajaran untuk menghadapi abad 21"),
                new Product("Thinking, Fast and Slow - Kahneman", 275000, 60, "Buku psikologi tentang dua sistem pengambilan keputusan"),
                new Product("The Power of Habit - Charles Duhigg", 165000, 105, "Mengubah kebiasaan dengan sains dan strategi"),
                new Product("Start with Why - Simon Sinek", 155000, 115, "Pentingnya know your why dalam leadership dan bisnis"),
                new Product("Leaders Eat Last - Simon Sinek", 185000, 95, "Tentang creating circle of safety dalam organisasi"),
                new Product("The 7 Habits of Highly Effective People", 215000, 125, "Klasik pengembangan diri Stephen Covey"),
                new Product("How to Win Friends & Influence People", 145000, 160, "Klasik Dale Carnegie tentang hubungan sosial"),
                new Product("The 4-Hour Workweek - Tim Ferriss", 190000, 105, "Lifestyle design dan escape 9-5"),
                new Product("Deep Work - Cal Newport", 175000, 120, "Fokus dalam dunia yang penuh distraksi"),
                new Product("Digital Minimalism - Cal Newport", 165000, 90, "Teknologi yang berpihak pada nilai-nilai hidup"),
                new Product("The Psychology of Money - Morgan Housel", 145000, 150, "Psychological aspects of money"),
                new Product("The Simple Path to Wealth - JL Collins", 175000, 130, "Investasi sederhana untuk menuju kemakmuran"),
                new Product("A Random Walk Down Wall Street", 225000, 70, "Investasi jangka panjang dengan efficient market hypothesis"),
                new Product("The Intelligent Investor - Benjamin Graham", 280000, 55, "Bible of value investing"),
                new Product("Common Stocks and Uncommon Profits", 250000, 45, "Investasi saham dengan fokus pada company quality"),
                
                // Food & Beverages - 35 products
                new Product("Kopi Arabika Toraja 250g", 85000, 100, "Kopi arabika premium dari Toraja dengan cita rasa khas dan aroma semerbak"),
                new Product("Kopi Robusta Munthe", 65000, 120, "Kopi robusta kuat dengan bodyfull, cocok untuk kopi tubruk"),
                new Product("Kopi Luwak Original 100g", 275000, 30, "Kopi luwak asli dengan proses sorting manual, rasa smooth"),
                new Product("Teh Celup Poci Sariwangi", 35000, 200, "Teh celup poci dengan aroma wangi dan rasa kuat"),
                new Product("Susu UHT ultra milk 1L", 18000, 300, "Susu sapi segar full cream dengan kualitas tinggi"),
                new Product("Indomie Goreng Special", 4000, 500, "Mie instan goreng dengan bumbu rahasia dan topping melimpah"),
                new Product("Chitato Original 100g", 15000, 250, "Keripik kentang renyah dengan bumbu original"),
                new Product("Tango Wafer Coklat", 8000, 400, "Wafer tipis dengan selai coklat dan lapisan coklat"),
                new Product("Aqua Mineral Water 600ml", 5000, 800, "Air minum dalam kemasan steril dan aman"),
                new Product("Coca-Cola 330ml", 8000, 450, "Minuman bersoda klasik dengan rasa original"),
                new Product("Pocari Sweat 500ml", 9000, 380, "Minuman isotonik untuk mengganti cairan tubuh"),
                new Product("Mizone 600ml", 10000, 320, "Minuman sport dengan elektrolit dan vitamin"),
                new Product("Teh botol Sosro 500ml", 7000, 420, "Teh manis segar dengan rasa autentik"),
                new Product("Ultra Milk 1L", 18000, 280, "Susu UHT dengan berbagai pilihan rasa"),
                new Product("Cimory UHT 250ml", 5000, 550, "Susu sapi segar dari peternakan terpercaya"),
                new Product("Yakult 80ml", 8000, 380, "Minuman fermentasi dengan probiotik Lactobacillus"),
                new Product("Nestle Milo 200g", 35000, 150, "Serbuk coklat malt dengan tambahan kalsium dan vitamin"),
                new Product("Beras Wangi Pandan Wangi 5kg", 85000, 60, "Beras premium dengan aroma wangi dan tekstur pulen"),
                new Product("Minyak Goreng tropical 2L", 45000, 120, "Minyak Goreng dengan teknologi jernih dan rendah kolesterol"),
                new Product("Gula Merah Tebu 1kg", 28000, 140, "Gula merah asli dari tebu dengan rasa khas"),
                new Product("Kecap Bango 600ml", 22000, 180, "Kecap asin manis dengan kualitas premium"),
                new Product("Saus Sambal Indofood 335ml", 12000, 250, "Sambal siap saji dengan cita rasa authentic"),
                new Product("Bumbu Instan Sajiku 50g", 3000, 400, "Bumbu masak praktis dengan rasa autentik"),
                new Product("Indomie Kuah Rasa Ayam", 3500, 480, "Mie instan kuah dengan rasa ayam yang savory"),
                new Product("Sarimi Kuah Rasa Bawang", 3000, 520, "Mie instan sarimi dengan rasa bawang yang kuat"),
                new Product("Pop Mie Rasa Sapi", 5000, 320, "Mie instan cup dengan rasa sapi yang sedap"),
                new Product("Biskuit Roma", 8000, 280, "Biskuit kacang dengan rasa karamel"),
                new Product("Kacang Garuda 200g", 15000, 200, "Kacang kedelai goreng renyah dengan bumbu original"),
                new Product("Chitato Sour Cream Onion 100g", 16000, 190, "Keripik dengan rasa sour cream dan bawang"),
                new Product("Lay's Classic 120g", 22000, 150, "Keripik kentang asli dengan rasa original"),
                new Product("Tisi Jagung Bakar 150g", 12000, 230, "Keripik jagung dengan rasa bakar yang gurih"),
                new Product("Gery Original 200g", 18000, 160, "Biskuit cream dengan rasa original"),
                new Product("Kue Biskit", 8000, 270, "Kue basah dengan rasa vanila dan keju"),
                new Product("BreadTalk Butter Croissant", 18000, 140, "Croissant dengan butter grade tinggi"),
                new Product("Hoka Hoka Bento Original", 10000, 210, "Snack ring dengan rasa original crispy"),
                
                // Clothing - 35 products
                new Product("Kaos Polos Cotton Combed 30s", 45000, 200, "Kaos polos bahan cotton combed 30s, nyaman dan menyerap keringat"),
                new Product("Kemeja Flannel Kotak Merah", 125000, 80, "Kemeja flannel dengan motif kotak, bahan tebal dan hangat"),
                new Product("Hoodie Oversize Basic", 175000, 60, "Hoodie oversized dengan bahan fleece, casual dan comfortable"),
                new Product("Jaket Bomber Polyester", 220000, 45, "Jaket bomber dengan bahan polyester, water resistant"),
                new Product("Celana Kulot Denim", 185000, 70, "Celana kulot dengan bahan denim, potongan wide-leg"),
                new Product("Celana Chino Slim Fit", 155000, 85, "Celana chino dengan potongan slim fit, warna beige"),
                new Product("Rok Plisket Pinggang Karet", 95000, 110, "Rok plisket dengan pinggang karet, lapisan pendamping dalam"),
                new Product("Blouse Satin Plain", 135000, 55, "Blouse dengan bahan satin, glossy dan elegan"),
                new Product("Dress Floral Maxi", 225000, 40, "Dress panjang dengan motif floral, bahan cotton mixed"),
                new Product("Celana Pendek Chino", 95000, 130, "Celana pendek chino cotton, untuk casual dan formal"),
                new Product("Kaos Komoro Original", 35000, 250, "Kaos komoro dengan bahan cotton combed 30s, original color"),
                new Product("Kemeja Linen Premium", 165000, 65, "Kemeja linen dengan bahan premium, adem dan stylish"),
                new Product("Sweater Crewneck Basic", 125000, 95, "Sweater crewneck dengan bahan cotton polyester, simple design"),
                new Product("Parka Windbreaker", 245000, 35, "Jaket windbreaker dengan teknologi anti-angin dan water resistant"),
                new Product("Celana Cargo Tactical", 195000, 50, "Celana cargo dengan banyak kantong, bahan ripstop"),
                new Product("Kaos Polo Pique", 85000, 180, "Kaos polo dengan bahan pique cotton, elegant dan casual"),
                new Product("Kemeja Batik Premium", 350000, 40, "Kemeja batik dengan motif tradisional premium, katun stretch"),
                new Product("Jeans Slim Fit Black", 225000, 75, "Jeans slim fit warna hitam, bahan stretch denim"),
                new Product("Celana Jogger Sweat", 115000, 100, "Celana jogger dengan bahan sweat, untuk sport dan casual"),
                new Product("Blazer Casual Polyester", 265000, 30, "Blazer casual dengan bahan polyester, suitable untuk formal"),
                new Product("Kaos Crop Top", 65000, 140, "Kaos crop top wanita, casual dan trendy"),
                new Product("Kemeja Oxford Button Down", 175000, 55, "Kemeja oxford dengan kerah button down, bahan cotton"),
                new Product("T-Shirt Graphic Unisex", 85000, 160, "Kaos graphic dengan desain unisex, bahan cotton combed"),
                new Product("Celana Kulot Wide Leg", 195000, 45, "Celana kulot wide leg, bahan rayon, flowy dan nyaman"),
                new Product("Hoodie Zip Full Zip", 165000, 65, "Hoodie dengan full zipper, bahan fleece hangat"),
                new Product("Kerudung Premium Kotak", 75000, 190, "Kerudung dengan bahan premium, mudah dipakai dan stylish"),
                new Product("Kemeja Denim Light Wash", 145000, 70, "Kemeja denim dengan warna light wash, casual style"),
                new Product("Jumpsuit Casual", 215000, 25, "Jumpsuit one-piece dengan design casual, practical"),
                new Product("Blouse Katun Premium", 125000, 80, "Blouse cotton premium, elegan untuk work wear"),
                new Product("Celana Panjang Formal", 265000, 35, "Celana formal untuk acara resmi, bahan wool mix"),
                new Product("Jacket Leather Synthetic", 345000, 20, "Jacket kulit sintetis dengan design biker, stylish"),
                new Product("Sweatshirt Printed", 135000, 55, "Sweatshirt dengan print grafis, bahan cotton fleece"),
                new Product("Pants Tactical Cargo", 175000, 40, "Celana tactical dengan pocket banyak, durable"),
                new Product("T-Shirt Linen Blend", 95000, 120, "Kaos linen cotton blend, adem dan tidak kusut"),
                new Product("Cardigan Open Front", 165000, 45, "Cardigan dengan model open front, bahan wool blend"),
                new Product("Skirt High Waist A-line", 115000, 50, "Rok A-line high waist, bahan denim stretch"),
                
                // Sports & Fitness - 25 products
                new Product("Sepatu Lari Nike Air Zoom Pegasus", 1850000, 25, "Sepatu lari dengan teknologi Zoom Air, responsive cushioning"),
                new Product("Adidas Ultraboost 23", 2200000, 20, "Sepatu lari dengan Boost midsole, energy return tinggi"),
                new Product("Pakaian Olahraga Adidas Tiro", 350000, 60, "Pakaian olahraga dengan teknologi moisture-wicking"),
                new Product("Yoga Mat Premium 6mm", 250000, 45, "Mat yoga tebal 6mm dengan anti-slip surface"),
                new Product("Dumbbell Set 20kg", 850000, 15, "Set dumbbell dengan plat iron, adjustable"),
                new Product("Resistance Band Set", 175000, 80, "Set resistance band dengan berbagai level ketahanan"),
                new Product("Racket Bulu Tangkis Yonex", 1250000, 18, "Racket bulu tangkis profesional dengan power dan kontrol"),
                new Product("Bola Sepak Adidas UCL", 450000, 30, "Bola sepak resmi UEFA Champions League"),
                new Product("Gym Bag Large 40L", 275000, 40, "Tas gym besar dengan compartment banyak, waterproof"),
                new Product("Water Bottle Tritan 750ml", 95000, 120, "Botol minum dengan bahan tritan tahan pecah, tidak berbau"),
                new Product("Jump Rope Speed", 85000, 90, "Rope skipping dengan bearing fast rotation, adjustable length"),
                new Product("Pull-Up Bar Door Mount", 195000, 25, "Bar pull-up yang dipasang di pintu tanpa bor"),
                new Product("Foam Roller 33cm", 120000, 55, "Foam roller untuk relaksasi otot dan myofascial release"),
                new Product("Kettlebell 16kg", 350000, 20, "Kettlebell cast iron untuk strength training"),
                new Product("Boxing Gloves 12oz", 550000, 15, "Sarung tangan boxing dengan padding tebal, wrist support"),
                new Product("Headphone Wireless Sport", 650000, 35, "Headphone sport wireless dengan ear hooks, sweatproof"),
                new Product("Smart Watch Fitness Tracker", 1200000, 28, "Smartwatch dengan heart rate monitor, GPS, 7-day battery"),
                new Product("Cycling Helmet", 450000, 12, "Helmet bersepeda dengan ventilation dan adjustable strap"),
                new Product("Treadmill Foldable 2HP", 8500000, 5, "Treadmill lipat dengan motor 2HP, incline adjustable"),
                new Product("Yoga Block Pack 2", 85000, 70, "Set yoga block dari high-density foam"),
                new Product("Protein Whey Isolate 2kg", 850000, 25, "Protein whey isolate untuk muscle gain, low lactose"),
                new Product("Pre-Workout Energy", 275000, 40, "Supplement pre-workout untuk energy boost dan focus"),
                new Product("Creatine Monohydrate", 175000, 60, "Supplement creatine untuk strength dan power"),
                new Product("Multivitamin Sport", 195000, 50, "Vitamin untuk atlet dan active lifestyle"),
                new Product("Elastic Band Set 5-levels", 125000, 85, "Set elastic band dengan berbagai resistance level")
            );

            productRepository.saveAll(products);
            System.out.println("Database initialized with 150 products and 5 categories");
        }
    }
}
